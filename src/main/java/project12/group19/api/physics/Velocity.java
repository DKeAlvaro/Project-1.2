package project12.group19.api.physics;

import java.util.function.DoubleUnaryOperator;

/**
 * A simple structure to hold information
 *
 * TODO: replace Player.Hit with this
 */
public interface Velocity {
    /**
     * @return x-axis component, in meters per second
     */
    double getXVelocity();

    /**
     * @return y-axis component, in meters per second
     */
    double getYVelocity();

    /**
     * @return Absolute value for velocity, in meters per second.
     */
    default double getAbsoluteVelocity() {
        return Math.sqrt(Math.pow(getXVelocity(), 2) + Math.pow(getYVelocity(), 2));
    }

    /**
     * @return Angle at which velocity is applied. Please note that this
     * is in radians, not degrees.
     */
    default double getVelocityAngle() {
        return Math.atan2(getYVelocity(), getXVelocity());
    }

    default Velocity mapAbsoluteVelocity(DoubleUnaryOperator transformer) {
        double mutated = transformer.applyAsDouble(getAbsoluteVelocity());
        return polar(mutated, getVelocityAngle());
    }

    default Velocity mapVelocityAngle(DoubleUnaryOperator transformer) {
        double mutated = transformer.applyAsDouble(getVelocityAngle());
        return polar(getAbsoluteVelocity(), mutated);
    }

    default Velocity deflectVelocity(double radians) {
        return polar(getAbsoluteVelocity(), getVelocityAngle() + radians);
    }

    default Velocity scaleVelocity(double xMultiplier, double yMultiplier) {
        return euclidian(getXVelocity() * xMultiplier, getYVelocity() * yMultiplier);
    }

    default Velocity scaleVelocity(double multiplier) {
        return scaleVelocity(multiplier, multiplier);
    }

    default Velocity scaleXVelocity(double multiplier) {
        return euclidian(getXVelocity() * multiplier, getYVelocity());
    }

    default Velocity scaleYVelocity(double multiplier) {
        return euclidian(getXVelocity(), getYVelocity() * multiplier);
    }

    default Velocity addVelocity(double xAddendum, double yAddendum) {
        return euclidian(getXVelocity() + xAddendum, getYVelocity() + yAddendum);
    }

    default Velocity addXVelocity(double addendum) {
        return euclidian(getXVelocity() + addendum, getYVelocity());
    }

    default Velocity addYVelocity(double addendum) {
        return euclidian(getXVelocity(), getYVelocity() + addendum);
    }

    default Velocity addToAbsoluteVelocity(double addendum) {
        return polar(getAbsoluteVelocity() + addendum, getVelocityAngle());
    }

    static Velocity euclidian(double xVelocity, double yVelocity) {
        return new Standard(xVelocity, yVelocity);
    }
    static Velocity polar(double velocity, double angle) {
        return new Standard(velocity * Math.cos(angle), velocity * Math.sin(angle));
    }

    record Standard(double x, double y) implements Velocity {
        @Override
        public double getXVelocity() {
            return x;
        }

        @Override
        public double getYVelocity() {
            return y;
        }
    }
}
