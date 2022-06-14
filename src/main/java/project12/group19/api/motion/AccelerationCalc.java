package project12.group19.api.motion;

import project12.group19.api.geometry.space.HeightProfile;

/**
 * This is an interface for acceleration calculator, which can be
 * implemented with different approaches in mind, for example,
 * simplifying calculations by ruling out some low-impact equation
 * components.
 */
public interface AccelerationCalc {
     static double accelerationX(HeightProfile profile, MotionState motion, Friction f, double scale) {
         return 0;
     }

     static double accelerationY(HeightProfile profile, MotionState motion, Friction f, double scale) {
         return 0;
     }
}
