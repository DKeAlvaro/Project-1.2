package project12.group19.api.motion;

public interface MotionState {
    double getXSpeed();
    double getYSpeed();
    double getXPosition();
    double getYPosition();

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
