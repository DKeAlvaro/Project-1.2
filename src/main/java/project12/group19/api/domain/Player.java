package project12.group19.api.domain;

import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.motion.MotionState;
import project12.group19.api.physics.Velocity;

import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * Describes a player. On each tick when ball is stable player is asked
 * whether to make a hit, which it can either do (by returning a filled
 * optional) or refuse to (by returning empty optional).
 */
public interface Player {
    Optional<Hit> play(State state) throws FileNotFoundException;
    default Optional<PlanarCoordinate> position(PlanarCoordinate start, PlanarCoordinate end) {
        return Optional.of(start);
    }

    interface Hit extends Velocity {
        default MotionState apply(MotionState state) {
            return state.withSpeed(state.getXSpeed() + getXVelocity(), state.getYSpeed() + getYVelocity());
        }

        record Standard(double xVelocity, double yVelocity) implements Hit {
            @Override
            public double getXVelocity() {
                return xVelocity;
            }

            @Override
            public double getYVelocity() {
                return yVelocity;
            }
        }

        static Hit create(double xVelocity, double yVelocity) {
            return new Standard(xVelocity, yVelocity);
        }

        static Hit polar(double velocity, double angle) {
            return new Standard(velocity * Math.cos(angle), velocity * Math.sin(angle));
        }

        static Hit create(Velocity velocity) {
            return new Standard(velocity.getXVelocity(), velocity.getYVelocity());
        }
    }
}
