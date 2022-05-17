package project12.group19.api.motion;

import project12.group19.api.geometry.space.HeightProfile;

public class AccCalculator {

    public static final double MOTION_ERROR = 1E-6;
    private static final double g = 9.81;
    public AccCalculator(){

    }

    /**
     * Computes the acceleration in direction X, taking into account if Vx and Vy are both 0 or not
     * @param profile HeightProfile
     * @param motion Motion State
     * @param f friction FrictionC
     * @return
     */
    public static double accelerationX(HeightProfile profile, MotionState motion, Friction f, double scale){
        double accX;
        double dhdx = Derivative.derivativeHX(profile, motion.getXPosition(), motion.getYPosition());
        double dhdy = Derivative.derivativeHY(profile, motion.getXPosition(), motion.getYPosition());
        double dh = Math.sqrt(Math.pow(dhdx, 2) + Math.pow(dhdy, 2));


        if (!(StopCondition.isVelocity0(motion, scale)) ){

            accX = (-1) * g * (dhdx) - (f.getDynamicCoefficient() * g * motion.getXSpeed()) / Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2));
        } else if (Math.abs(dh) > MOTION_ERROR * scale) {
            accX = (-1) * g * (dhdx) - (f.getDynamicCoefficient() * g * dhdx / dh);


        } else {
            return 0;

        }
        return accX;
    }

    /**
     * Computes the acceleration in direction Y, taking into account if Vx and Vy are both 0 or not
     * @param profile HeightProfile
     * @param motion Motion State
     * @param f friction FrictionC
     * @return
     */
    public static double accelerationY(HeightProfile profile, MotionState motion, Friction f, double scale){

        double accY;
        double dhdx = Derivative.derivativeHX(profile, motion.getXPosition(), motion.getYPosition());
        double dhdy = Derivative.derivativeHY(profile, motion.getXPosition(), motion.getYPosition());
        double dh = Math.sqrt(Math.pow(dhdx, 2) + Math.pow(dhdy, 2));


        if (!(StopCondition.isVelocity0(motion, scale))) {
            accY = (-1) * g * (dhdy) - (f.getDynamicCoefficient() * g * motion.getYSpeed()) / Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2));

        } else if (Math.abs(dh) > MOTION_ERROR * scale) {

            accY = (-1) * g * (dhdy) - (f.getDynamicCoefficient() * g * dhdy / dh);

        } else {
            return 0;

        }
        return accY;
    }

}
