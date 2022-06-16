package project12.group19.api.motion;

import project12.group19.api.geometry.space.HeightProfile;

public class AccCalculatorSecondOrder implements AccelerationCalculator {
    public static final double MOTION_ERROR = 1E-6;
    private static final double g = 9.81;
    public AccCalculatorSecondOrder(){

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
        double der2 = Math.pow(dhdx, 2)+ Math.pow(dhdy, 2); //sum of squared derivatives dhdx and dhdy
        double dh = Math.sqrt(der2);



        if (!(StopCondition.isVelocity0(motion, scale)) ){

            accX = (-1) * g * (dhdx)/(1+ der2) - (f.getDynamicCoefficient() * g * motion.getXSpeed()) /
                    (Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2)+
                            Math.pow(dhdx* motion.getXSpeed()+dhdy* motion.getYSpeed(), 2) *Math.sqrt(1+ der2)));
        } else if (Math.abs(dh) > MOTION_ERROR * scale && dh > f.getStaticCoefficient()) {
            accX = (-1) * g * (dhdx)/(der2 +1) - (f.getDynamicCoefficient() * g * dhdx / (Math.sqrt(1+ der2)* Math.sqrt(der2 +Math.pow(der2, 2))));


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
        double der2 = Math.pow(dhdx, 2)+ Math.pow(dhdy, 2); //sum of squared derivatives dhdx and dhdy
        double dh = Math.sqrt(der2);


        if (!(StopCondition.isVelocity0(motion, scale))) {
            accY = ((-1) * g * (dhdy)/(1+ der2) )- (f.getDynamicCoefficient() * g * motion.getYSpeed()) /
                    (Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2)+
                            Math.pow(dhdx* motion.getXSpeed()+dhdy* motion.getYSpeed(), 2) *Math.sqrt(1+ der2)));

        } else if (Math.abs(dh) > MOTION_ERROR * scale && dh > f.getStaticCoefficient()) {

            accY = (-1) * g * (dhdy)/(der2 +1) - (f.getDynamicCoefficient() * g * dhdy / (Math.sqrt(1+ der2)* Math.sqrt(der2 +Math.pow(der2, 2))));

        } else {
            return 0;

        }
        return accY;
    }

}
