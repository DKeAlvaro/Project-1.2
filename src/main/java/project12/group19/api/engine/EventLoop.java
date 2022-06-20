package project12.group19.api.engine;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface EventLoop {

    interface Iteration<S> {
        boolean isFinal();
        S getState();

        record Standard<S>(boolean isFinal, S state) implements Iteration<S> {
            @Override
            public S getState() {
                return state;
            }
        }

        static <S> Iteration<S> intermediate(S value) {
            return new Iteration.Standard<>(false, value);
        }

        static <S> Iteration<S> terminal(S value) {
            return new Iteration.Standard<>(true, value);
        }

        static <S> Iteration<S> create(S value, boolean isFinal) {
            return isFinal ? terminal(value) : intermediate(value);
        }
    }
    interface Task<S> {

        Iteration<S> execute(S state);
    }

    <S> CompletableFuture<S> submit(Task<S> task, S bootstrap, long interval, TimeUnit unit);

    default <S> CompletableFuture<S> submit(Task<S> task, long interval, TimeUnit unit) {
        return submit(task, null, interval, unit);
    }
}
