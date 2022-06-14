package project12.group19.api.motion;

import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.physics.Velocity;

/**
 * State vector for the single moving item in the game, holds item
 * position and velocity.
 *
 * TODO: fully deprecate x/y speed methods and rely on velocity methods
 * only.
 */
public interface MotionState extends Velocity {
    double getXSpeed();
    double getYSpeed();
    double getXPosition();
    double getYPosition();

    @Override
    default double getXVelocity() {
        return getXSpeed();
    }

    @Override
    default double getYVelocity() {
        return getYSpeed();
    }

    default MotionState withXSpeed(double xSpeed) {
        return new Standard(xSpeed, getYSpeed(), getXPosition(), getYPosition());
    }

    default MotionState withYSpeed(double ySpeed) {
        return new Standard(getXSpeed(), ySpeed, getXPosition(), getYPosition());
    }

    default MotionState withXPosition(double xPosition) {
        return new Standard(getXSpeed(), getYSpeed(), xPosition, getYPosition());
    }

    default MotionState withYPosition(double yPosition) {
        return new Standard(getXSpeed(), getYSpeed(), getXPosition(), yPosition);
    }

    default double getAbsoluteSpeed() {
        return Math.sqrt(getXSpeed() * getXSpeed() + getYSpeed() * getYSpeed());
    }

    default PlanarCoordinate getPosition() {
        return PlanarCoordinate.create(getXPosition(), getYPosition());
    }

    static MotionState zero() {
        return new Standard(0, 0, 0, 0);
    }

    static MotionState create(Velocity velocity, PlanarCoordinate position) {
        return new Standard(velocity.getXVelocity(), velocity.getYVelocity(), position.getX(), position.getY());
    }

    static MotionState create(double xVelocity, double yVelocity, double xPosition, double yPosition) {
        return new Standard(xVelocity, yVelocity, xPosition, yPosition);
    }

    record Standard(
            double xSpeed,
            double ySpeed,
            double xPosition,
            double yPosition
    ) implements MotionState {
        @Override
        public double getXSpeed() {
            return xSpeed;
        }

        @Override
        public double getYSpeed() {
            return ySpeed;
        }

        @Override
        public double getXPosition() {
            return xPosition;
        }

        @Override
        public double getYPosition() {
            return yPosition;
        }
    }
}
