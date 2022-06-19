package project12.group19.physics.motion;

import project12.group19.api.domain.Surface;
import project12.group19.api.physics.motion.AccelerationCalculator;
import project12.group19.api.physics.motion.Friction;
import project12.group19.api.physics.motion.MotionState;
import project12.group19.math.Derivative;

public class AdvancedAccelerationCalculator implements AccelerationCalculator {
    public static final double MOTION_ERROR = 1E-6;
    private static final double G = 9.81;

    /**
     * Computes the acceleration in direction X, taking into account if Vx and Vy are both 0 or not
     * @param surface HeightProfile
     * @param motion Motion State
     * @return
     */
    @Override
    public double getXAcceleration(Surface surface, MotionState motion, double scale){
        double accX;
        double dhdx = Derivative.derivativeHX(surface, motion.getXPosition(), motion.getYPosition());
        double dhdy = Derivative.derivativeHY(surface, motion.getXPosition(), motion.getYPosition());
        double der2 = Math.pow(dhdx, 2)+ Math.pow(dhdy, 2); //sum of squared derivatives dhdx and dhdy
        double dh = Math.sqrt(der2);
        Friction f = surface.getFriction(motion.getPosition());

        if (!(StopCondition.isVelocity0(motion, scale)) ){

            accX = (-1) * G * (dhdx)/(1+ der2) - (f.getDynamicCoefficient() * G * motion.getXSpeed()) /
                    (Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2)+
                            Math.pow(dhdx* motion.getXSpeed()+dhdy* motion.getYSpeed(), 2) *Math.sqrt(1+ der2)));
        } else if (Math.abs(dh) > MOTION_ERROR * scale && dh > f.getStaticCoefficient()) {
            accX = (-1) * G * (dhdx)/(der2 +1) - (f.getDynamicCoefficient() * G * dhdx / (Math.sqrt(1+ der2)* Math.sqrt(der2 +Math.pow(der2, 2))));
        } else {
            return 0;

        }
        return accX;
    }

    /**
     * Computes the acceleration in direction Y, taking into account if Vx and Vy are both 0 or not
     * @param surface HeightProfile
     * @param motion Motion State
     * @return
     */
    @Override
    public double getYAcceleration(Surface surface, MotionState motion, double scale){
        double accY;
        double dhdx = Derivative.derivativeHX(surface, motion.getXPosition(), motion.getYPosition());
        double dhdy = Derivative.derivativeHY(surface, motion.getXPosition(), motion.getYPosition());
        double der2 = Math.pow(dhdx, 2)+ Math.pow(dhdy, 2); //sum of squared derivatives dhdx and dhdy
        double dh = Math.sqrt(der2);
        Friction f = surface.getFriction(motion.getPosition());

        if (!(StopCondition.isVelocity0(motion, scale))) {
            accY = ((-1) * G * (dhdy)/(1+ der2) )- (f.getDynamicCoefficient() * G * motion.getYSpeed()) /
                    (Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2)+
                            Math.pow(dhdx* motion.getXSpeed()+dhdy* motion.getYSpeed(), 2) *Math.sqrt(1+ der2)));

        } else if (Math.abs(dh) > MOTION_ERROR * scale && dh > f.getStaticCoefficient()) {

            accY = (-1) * G * (dhdy)/(der2 +1) - (f.getDynamicCoefficient() * G * dhdy / (Math.sqrt(1+ der2)* Math.sqrt(der2 +Math.pow(der2, 2))));

        } else {
            return 0;

        }
        return accY;
    }

}
