package project12.group19.api.motion;

import java.util.OptionalDouble;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Movement plotter is a component that plots ball movement till one of
 * terminal condition occurs (stop, drowning, out-of-bounds, etc.) for
 * provided initial motion state, calculating some product value on each
 * step. Bots can use it to evaluate specific hit.
 */
public interface MovementPlotter {
    <T> T calculate(MotionState state, double step, BiFunction<T, MotionResult, T> reducer, T initial);
    default <T> T calculate(MotionState state, double step, BiFunction<T, MotionResult, T> reducer) {
        return calculate(state, step, reducer, null);
    }
    default MotionResult calculate(MotionState state, double step) {
        return calculate(state, step, ((previous, updated) -> updated));
    }

    interface Iteration<T> {
        long getIndex();
        MotionResult getResult();
        T getAccumulator();
        double getStep();

        record Standard<T>(long index, MotionResult result, T accumulator, double step) implements Iteration<T> {
            @Override
            public long getIndex() {
                return index;
            }

            @Override
            public MotionResult getResult() {
                return result;
            }

            @Override
            public T getAccumulator() {
                return accumulator;
            }

            @Override
            public double getStep() {
                return step;
            }
        }
    }

    interface Evaluation<T> {
        boolean shouldTerminate();
        OptionalDouble getStep();
        T getAccumulator();

        record Standard<T>(boolean shouldTerminate, OptionalDouble step, T accumulator) implements Evaluation<T> {
            @Override
            public OptionalDouble getStep() {
                return step;
            }

            @Override
            public T getAccumulator() {
                return accumulator;
            }
        }
    }

    interface Input<T> {
        MotionState getInitialState();
        double getStep();
        T createAccumulator();
        Function<Iteration<T>, Evaluation<T>> getReducer();

        record Standard<T>(
                MotionState initialState,
                double step,
                T accumulator,
                Function<Iteration<T>, Evaluation<T>>
        ) implements Input<T> {}
    }

    interface Output<T> {
        long getIterations();
        boolean isPrematurelyTerminated();
        MotionResult getResult();
        T getAccumulator();
    }
}
