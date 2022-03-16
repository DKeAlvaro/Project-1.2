package project12.group19.engine;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class EventLoop {
    private final ScheduledExecutorService scheduler;

    public EventLoop(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    public CompletableFuture<Void> schedule(Task task, long interval, TimeUnit unit) {
        Handle handle = new Handle(scheduler, task, unit.toNanos(interval));
        scheduler.schedule(handle, 0, TimeUnit.MICROSECONDS);
        return handle.getCompletion();
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    public boolean awaitTermination(long interval, TimeUnit unit) throws InterruptedException {
        return scheduler.awaitTermination(interval, unit);
    }

    interface Task {
        boolean run(long epoch);
    }

    private static class Handle implements Runnable {
        private final CompletableFuture<Void> completion = new CompletableFuture<>();
        private final AtomicLong epoch = new AtomicLong();

        private final ScheduledExecutorService scheduler;
        private final Task task;
        private final long interval;

        public Handle(ScheduledExecutorService scheduler, Task task, long interval) {
            this.scheduler = scheduler;
            this.task = task;
            this.interval = interval;
        }

        public CompletableFuture<Void> getCompletion() {
            return completion;
        }

        @Override
        public void run() {
            try {
                long start = System.nanoTime();
                if (task.run(epoch.getAndIncrement())) {
                    long elapsed = System.nanoTime() - start;
                    long delay = interval - elapsed;
                    scheduler.schedule(this, delay, TimeUnit.NANOSECONDS);
                } else {
                    completion.complete(null);
                }
            } catch (Throwable e) {
                completion.completeExceptionally(e);
            }
        }
    }
}
