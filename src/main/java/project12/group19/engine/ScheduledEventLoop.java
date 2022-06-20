package project12.group19.engine;

import project12.group19.api.engine.EventLoop;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ScheduledEventLoop implements EventLoop {
    private static final AtomicReference<ScheduledEventLoop> DEFAULT = new AtomicReference<>();

    private final ScheduledExecutorService scheduler;

    public ScheduledEventLoop(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public <S> CompletableFuture<S> submit(Task<S> task, S bootstrap, long interval, TimeUnit unit) {
        long delay = unit.toNanos(interval);

        Execution<S> execution = createExecution(task, bootstrap, delay);

        scheduler.schedule(execution, 0, TimeUnit.NANOSECONDS);

        return execution.getCompletion();
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    public boolean awaitTermination(long interval, TimeUnit unit) throws InterruptedException {
        return scheduler.awaitTermination(interval, unit);
    }

    private <S> Execution<S> createExecution(Task<S> task, S bootstrap, long interval) {
        if (interval == 0) {
            return new ImmediateExecution<>(task, bootstrap);
        }

        return new ScheduledExecution<>(task, bootstrap, scheduler, interval);
    }

    private interface Execution<S> extends Runnable {
        CompletableFuture<S> getCompletion();
    }

    private static class ScheduledExecution<S> implements Execution<S> {
        private final CompletableFuture<S> completion = new CompletableFuture<>();

        private final EventLoop.Task<S> task;
        private final AtomicReference<S> state;
        private final ScheduledExecutorService executor;
        private final long interval;

        public ScheduledExecution(Task<S> task, S bootstrap, ScheduledExecutorService executor, long interval) {
            this.task = task;
            this.state = new AtomicReference<>(bootstrap);
            this.executor = executor;
            this.interval = interval;
        }

        @Override
        public void run() {
            try {
                long start = System.nanoTime();
                Iteration<S> iteration = task.execute(state.get());
                state.set(iteration.getState());

                if (iteration.isFinal()) {
                    completion.complete(iteration.getState());
                    return;
                }

                long elapsed = System.nanoTime() - start;
                long delay = Math.max(0, interval - elapsed);

                // TODO: If remaining is <= 0, do not schedule execution, run as-is in while loop
                executor.schedule(this, delay, TimeUnit.NANOSECONDS);
            } catch (RuntimeException e) {
                completion.completeExceptionally(e);
            }
        }

        @Override
        public CompletableFuture<S> getCompletion() {
            return completion;
        }
    }

    private record ImmediateExecution<S>(EventLoop.Task<S> task, S bootstrap, CompletableFuture<S> completion) implements Execution<S> {
        public ImmediateExecution(EventLoop.Task<S> task, S bootstrap) {
            this(task, bootstrap, new CompletableFuture<>());
        }

        @Override
        public void run() {
            S state = bootstrap;
            Iteration<S> iteration;
            try {
                do {
                    iteration = task.execute(state);
                    state = iteration.getState();
                } while (!iteration.isFinal());
                completion.complete(state);
            } catch (RuntimeException e) {
                completion.completeExceptionally(e);
            }
        }

        @Override
        public CompletableFuture<S> getCompletion() {
            return completion;
        }
    }

    public static EventLoop standard() {
        if (DEFAULT.get() == null) {
            synchronized (DEFAULT) {
                if (DEFAULT.get() != null) {
                    return DEFAULT.get();
                }

                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
                        Runtime.getRuntime().availableProcessors(),
                        StandardThreadFactory.daemon("default-event-loop-")
                );

                ScheduledEventLoop loop = new ScheduledEventLoop(scheduler);
                DEFAULT.set(loop);
            }
        }

        return DEFAULT.get();
    }
}
