package project12.group19.api.motion;

import project12.group19.api.domain.Surface;

public class BasicAccelerationCalculator implements AccelerationCalculator {
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
        double dh = Math.sqrt(Math.pow(dhdx, 2)+ Math.pow(dhdy, 2));
        Friction f = surface.getFriction(motion.getPosition());


        if (!(StopCondition.isVelocity0(motion, scale)) ){

            accX = (-1) * G * (dhdx) - (f.getDynamicCoefficient() * G * motion.getXSpeed()) /
                    Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2));
        } else if (Math.abs(dh) > MOTION_ERROR * scale && dh > f.getStaticCoefficient()) {
            accX = (-1) * G * (dhdx) - (f.getDynamicCoefficient() * G * dhdx / dh);


        } else {
            return 0;

        }
        return accX;
    }

    /**
     * Computes the acceleration in direction Y, taking into account if Vx and Vy are both 0 or not
     * @param surface HeightProfile
     * @param motion Motion State
     * @param scale Precision controlling number, see parent interface.
     * @return
     */
    @Override
    public double getYAcceleration(Surface surface, MotionState motion, double scale) {
        Friction friction = surface.getFriction(motion.getPosition());

        double accY;
        double dhdx = Derivative.derivativeHX(surface, motion.getXPosition(), motion.getYPosition());
        double dhdy = Derivative.derivativeHY(surface, motion.getXPosition(), motion.getYPosition());
        double dh = Math.sqrt(Math.pow(dhdx, 2)+ Math.pow(dhdy, 2));


        if (!(StopCondition.isVelocity0(motion, scale))) {
            accY = (-1) * G * (dhdy) - (friction.getDynamicCoefficient() * G * motion.getYSpeed()) /
                    Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2));

        } else if (Math.abs(dh) > MOTION_ERROR * scale && dh > friction.getStaticCoefficient()) {

            accY = (-1) * G * (dhdy) - (friction.getDynamicCoefficient() * G * dhdy / dh);

        } else {
            return 0;

        }
        return accY;
    }

}
