package project12.group19.api.motion;

import project12.group19.api.geometry.space.HeightProfile;

public class StopCondition {


    public StopCondition(){

    }
    public static final double MOTION_ERROR = 1E-6;
    /**
     * This method checks if the ball is still moving, should be called before computing next state
     * @param motionState Contains velocity x, velocity y, x, y
     * @param friction
     * @return True if the ball is moving, false if the ball stops

     */
    public boolean isMoving(HeightProfile profile, MotionState motionState, FrictionC friction){
        double x = motionState.getXPosition();
        double y = motionState.getYPosition();
        if(isVelocity0(motionState)){
            double dhdx = Derivative.derivativeHX(profile, x, y);
            double dhdy = Derivative.derivativeHY(profile, x, y);
            if(Math.abs(dhdx) < MOTION_ERROR && Math.abs(dhdy) <MOTION_ERROR  ){
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
    public static boolean isVelocity0(MotionState motionState){
        if(Math.abs(motionState.getXSpeed()) < MOTION_ERROR && Math.abs(motionState.getYSpeed()) < MOTION_ERROR){
            return true;
        }else{
            return false;
        }
    }
}
