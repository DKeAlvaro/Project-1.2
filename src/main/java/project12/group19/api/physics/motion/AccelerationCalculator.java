package project12.group19.api.physics.motion;

import project12.group19.api.domain.Surface;

/**
 * This is an interface for acceleration calculator, which can be
 * implemented with different approaches in mind, for example,
 * simplifying calculations by ruling out some low-impact equation
 * components.
 */
public interface AccelerationCalculator {
    /**
     * @param surface Surface over which item is moving
     * @param state Current moving item state.
     * @param scale A number defining which at which precision
     * calculator should operate, with 1 being as "standard". Lower
     * values means more precision. It makes sense to use step size for
     * it.
     * @return X-axis acceleration component.
     */
     double getXAcceleration(Surface surface, MotionState state, double scale);
    /**
     * @param surface Surface over which item is moving
     * @param state Current moving item state.
     * @param scale A number defining which at which precision
     * calculator should operate, with 1 being as "standard". Lower
     * values means more precision. It makes sense to use step size for
     * it.
     * @return Y-axis acceleration component.
     */
     double getYAcceleration(Surface surface, MotionState state, double scale);
}
