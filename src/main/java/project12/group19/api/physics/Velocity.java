package project12.group19.api.physics;

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

    static Velocity create(double xVelocity, double yVelocity) {
        return new Standard(xVelocity, yVelocity);
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
