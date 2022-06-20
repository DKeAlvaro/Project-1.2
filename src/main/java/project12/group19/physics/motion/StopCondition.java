package project12.group19.physics.motion;

import project12.group19.api.domain.Surface;
import project12.group19.api.physics.motion.MotionState;
import project12.group19.math.Derivative;

public class StopCondition {
    /**
     * This method checks if the ball is still moving, should be called before computing next state
     * @param motionState Contains velocity x, velocity y, x, y
     * @return True if the ball is moving, false if the ball stops
     */
    public static boolean isMoving(Surface surface, MotionState motionState, double scale){
        double x = motionState.getXPosition();
        double y = motionState.getYPosition();
        if(isVelocity0(motionState, scale)){
            double dhdx = Derivative.derivativeHX(surface, x, y);
            double dhdy = Derivative.derivativeHY(surface, x, y);
            double threshold = threshold(scale);
            if(Math.abs(dhdx) < threshold && Math.abs(dhdy) < threshold){
                return false;
            }else if(surface.getFriction(x, y).getStaticCoefficient() > Math.sqrt(Math.pow(dhdx, 2)+ Math.pow(dhdy, 2))){
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
    }

    /**
     * Checks if both Vx and Vy are 0, if it is true, diffrent function for coputing acceleration should be used
     * @param motionState
     * @return True if both are 0, false otherwise
     */
    public static boolean isVelocity0(MotionState motionState, double step) {
        return motionState.getAbsoluteSpeed() < threshold(step);
    }

    private static double threshold(double step) {
        return Math.max(step, 1E-6);
    }
}
