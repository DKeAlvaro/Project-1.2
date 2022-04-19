package project12.group19.api.motion;

import project12.group19.api.geometry.space.HeightProfile;

public class RK2Solver implements MotionCalculator{
    public static final double MOTION_ERROR = 1E-6;
    private static final double h = 0.00000001;
    private static final double g = 9.81;
    @Override
    public MotionState calculate(MotionState state, Acceleration acceleration, double deltaT) {
        double x =0;
        double y=0;
        double xSpeed= 0;
        double ySpeed = 0;


        return new MotionState.Standard(xSpeed, ySpeed, x, y);
    }
    public double calcK1(double step, double t, double previous){
        return 0;
    }
    public double calcK2(double k1,double step, double t, double previous){
        return 0;
    }

     /** Computes the derivate of h with respect to X
     * @param x
     * @param y
     * @return
             */
    public static double derivativeHX(HeightProfile heightProfile, double x, double y){

        return (heightProfile.getHeight(x+h, y) - heightProfile.getHeight(x, y))/h;

    }
    /**
     * Computes the derivative of h with respect to Y
     * @param x
     * @param y
     * @return
     */
    public static double derivativeHY(HeightProfile heightProfile, double x, double y){
        return (heightProfile.getHeight(x, y+h) - heightProfile.getHeight(x,y))/h;

    }
    /**
     * Checks if both Vx and Vy are 0, if it is true, diffrent function for coputing acceleration should be used
     * @param motionState
     * @param deltaT
     * @return True if both are 0, false otherwise
     */
    public static boolean isVelocity0(MotionState motionState, double deltaT){
        if(motionState.getXSpeed() < deltaT/10 && motionState.getYSpeed() < deltaT/10){
            return true;
        }else{
            return false;
        }
    }
    /**
     * Computes the accelreation in direction X and Y, taking into account if Vx and Vy are both 0 or not
     * @param motion Motion State
     * @param f friction FrictionC
     * @param deltaT
     * @return
     */
    public static Acc acceleration(HeightProfile profile, MotionState motion, Friction f, double deltaT){
        double accX;
        double accY;
        double dhdx = derivativeHX(profile, motion.getXPosition(), motion.getYPosition());
        double dhdy = derivativeHY(profile, motion.getXPosition(), motion.getYPosition());
        double dh = Math.sqrt(dhdx * dhdx + dhdy * dhdy);

        if (!isVelocity0(motion, deltaT)) {
            accY = (-1) * g * (dhdy) - (f.getDynamicCoefficient() * g * motion.getYSpeed()) / Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2));
            accX = (-1) * g * (dhdx) - (f.getDynamicCoefficient() * g * motion.getXSpeed()) / Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2));
        } else if (Math.abs(dh) < MOTION_ERROR) {
            return new Acc(0, 0);
        } else {
            accX = (-1) * g * (dhdx) - f.getDynamicCoefficient() * g * dhdx / dh;
            accY = (-1) * g * (dhdy) - f.getDynamicCoefficient() * g * dhdy / dh;
        }

        return new Acc(accX, accY);
    }

    /**
     * This method checks if the ball is still moving, should be called before computing next state
     * @param motionState Contains velocity x, velocity y, x, y
     * @param friction
     * @param deltaT
     * @return True if the ball is moving, false if the ball stops

     */
    public boolean isMoving(HeightProfile profile, MotionState motionState, FrictionC friction, double deltaT){
        double x = motionState.getXPosition();
        double y = motionState.getYPosition();
        if(isVelocity0(motionState, deltaT)){
            double dhdx = derivativeHX(profile, x, y);
            double dhdy = derivativeHY(profile, x, y);
            if(dhdx ==0 && dhdy ==0 ){
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

}
