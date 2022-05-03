package project12.group19.api.motion;

public interface MotionState {
    double getXSpeed();
    double getYSpeed();
    double getXPosition();
    double getYPosition();

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

    static MotionState zero() {
        return new Standard(0, 0, 0, 0);
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
