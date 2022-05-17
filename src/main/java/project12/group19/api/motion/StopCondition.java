package project12.group19.api.motion;

import com.sun.jdi.event.StepEvent;
import project12.group19.api.geometry.space.HeightProfile;

public class StopCondition {


    public StopCondition(){

    }
    public static final double MOTION_ERROR = 1;
    /**
     * This method checks if the ball is still moving, should be called before computing next state
     * @param motionState Contains velocity x, velocity y, x, y
     * @param friction
     * @return True if the ball is moving, false if the ball stops

     */
    public static boolean isMoving(HeightProfile profile, MotionState motionState, Friction friction, double scale){
        double x = motionState.getXPosition();
        double y = motionState.getYPosition();
        if(isVelocity0(motionState, scale)){
            double dhdx = Derivative.derivativeHX(profile, x, y);
            double dhdy = Derivative.derivativeHY(profile, x, y);
            double threshold = threshold(scale);
            if(Math.abs(dhdx) < threshold && Math.abs(dhdy) < threshold){
                return false;
            }else if(friction.getStaticCoefficient() > Math.sqrt(Math.pow(dhdx, 2)+ Math.pow(dhdy, 2))){
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
    public static boolean isVelocity0(MotionState motionState, double step){
        double threshold = threshold(step);
        if(Math.abs(motionState.getXSpeed()) < threshold && Math.abs(motionState.getYSpeed()) < threshold){
            return true;
        }else{
            return false;
        }
    }

    private static double threshold(double step) {
        return Math.max(step, 1E-6);
    }
}
