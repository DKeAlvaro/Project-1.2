package project12.group19.motion;

import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.motion.Acceleration;
import project12.group19.api.motion.Friction;
import project12.group19.api.motion.MotionState;
import project12.group19.math.DerivativeEstimator;

import java.util.OptionalDouble;

public class AccelerationCalculator {
    private static final double GRAVITATIONAL_CONSTANT = 9.81;

    private final HeightProfile heightProfile;
    private final DerivativeEstimator derivation;
    private final Friction friction;

    public AccelerationCalculator(HeightProfile heightProfile, DerivativeEstimator derivation, Friction friction) {
        this.heightProfile = heightProfile;
        this.derivation = derivation;
        this.friction = friction;
    }

    public Acceleration getAcceleration(PlanarCoordinate coordinate, MotionState motion) {
        double dzdx = derivation
                .estimate(x -> OptionalDouble.of(heightProfile.getHeight(x, coordinate.getY())), coordinate.getX())
                .orElseThrow(() -> new IllegalArgumentException("Can't calculate dz/dx at " + coordinate.getX()));

        double dzdy = derivation
                .estimate(y -> OptionalDouble.of(heightProfile.getHeight(coordinate.getX(), y)), coordinate.getY())
                .orElseThrow(() -> new IllegalArgumentException("Can't calculate dz/dy at " + coordinate.getY()));

        double speed = Math.sqrt(square(motion.getXSpeed()) + square(motion.getYSpeed()));

        return new Acceleration.Standard(
                calculate(dzdx, friction.getDynamicCoefficient(), motion.getXSpeed(), speed),
                calculate(dzdy, friction.getDynamicCoefficient(), motion.getYSpeed(), speed)
        );
    }

    private static double calculate(double derivative, double friction, double directionSpeed, double absoluteSpeed) {
        return -GRAVITATIONAL_CONSTANT * (derivative + friction * directionSpeed / absoluteSpeed);
    }

    private static double square(double value) {
        return value * value;
    }
}
