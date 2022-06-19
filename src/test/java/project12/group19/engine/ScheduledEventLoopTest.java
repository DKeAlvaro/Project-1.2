package project12.group19.engine;

import org.junit.jupiter.api.Test;
import project12.group19.api.engine.EventLoop;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ScheduledEventLoopTest {
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors(),
            operation -> {
                Thread thread = new Thread(operation);
                thread.setDaemon(true);
                return thread;
            }
    );

    private static final EventLoop.Task<Integer> TASK = i -> EventLoop.Iteration.create(i + 1, i >= 4);

    @Test
    public void runsScheduledTask() {
        EventLoop sut = new ScheduledEventLoop(EXECUTOR_SERVICE);
        assertThat(sut.submit(TASK, 0, 1, TimeUnit.MILLISECONDS).join(), equalTo(5));
    }

    @Test
    public void runsImmediateTask() {
        EventLoop sut = new ScheduledEventLoop(EXECUTOR_SERVICE);
        assertThat(sut.submit(TASK, 0, 0, TimeUnit.MILLISECONDS).join(), equalTo(5));
    }
}
