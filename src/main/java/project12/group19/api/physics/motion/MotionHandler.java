package project12.group19.api.physics.motion;

/**
 * Motion handler is a simple extension over {@link MotionCalculator}.
 * If the calculator only gives next position and velocity, motion
 * handler also analyzes what does it mean for the ball - is it out of
 * bounds, is it in lake, is it moving, and so on.
 */
public interface MotionHandler {
    MotionResult next(MotionState state, double deltaT);
}
