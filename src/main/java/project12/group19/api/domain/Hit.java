package project12.group19.api.domain;

import project12.group19.api.motion.MotionState;
import project12.group19.api.physics.Velocity;

import java.util.List;

public interface Hit extends Velocity {
    default MotionState apply(MotionState state) {
        return state.withSpeed(state.getXSpeed() + getXVelocity(), state.getYSpeed() + getYVelocity());
    }

    default List<HitSimulation> getSimulations() {
        return List.of();
    }

    static Hit create(double xVelocity, double yVelocity, List<HitSimulation> simulations) {
        return new Standard(xVelocity, yVelocity, simulations);
    }

    static Hit create(double xVelocity, double yVelocity) {
        return create(xVelocity, yVelocity, List.of());
    }

    static Hit create(Velocity velocity, List<HitSimulation> simulations) {
        return create(velocity.getXVelocity(), velocity.getYVelocity(), simulations);
    }

    static Hit create(Velocity velocity) {
        return create(velocity, List.of());
    }

    static Hit polar(double velocity, double angle, List<HitSimulation> simulations) {
        return new Standard(velocity * Math.cos(angle), velocity * Math.sin(angle), simulations);
    }

    static Hit polar(double velocity, double angle) {
        return polar(velocity, angle, List.of());
    }

    record Standard(double xVelocity, double yVelocity, List<HitSimulation> simulations) implements Hit {
        @Override
        public double getXVelocity() {
            return xVelocity;
        }

        @Override
        public double getYVelocity() {
            return yVelocity;
        }

        @Override
        public List<HitSimulation> getSimulations() {
            return simulations;
        }

        @Override
        public String toString() {
            return "Hit.Standard [xVelocity=" + xVelocity + ", yVelocity=" + yVelocity +
                    ", absoluteVelocity=" + getAbsoluteVelocity() + ", angle=" + getVelocityAngle() + "]";
        }
    }
}
