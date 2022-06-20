package project12.group19.api.physics.motion;

import java.util.function.BiFunction;

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
}
