package project12.group19.engine;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class StandardThreadFactory implements ThreadFactory {
    private final AtomicInteger counter = new AtomicInteger(0);
    private final String prefix;
    private final boolean daemon;

    public StandardThreadFactory(String prefix, boolean daemon) {
        this.prefix = prefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(@NotNull Runnable task) {
        Thread thread = new Thread(task);
        thread.setDaemon(daemon);
        thread.setName(prefix + counter.incrementAndGet());
        return thread;
    }

    public static ThreadFactory daemon(String prefix) {
        return new StandardThreadFactory(prefix, true);
    }

    public static ThreadFactory regular(String prefix) {
        return new StandardThreadFactory(prefix, false);
    }
}
