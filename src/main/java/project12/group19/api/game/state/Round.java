package project12.group19.api.game.state;

import project12.group19.api.domain.Player;
import project12.group19.api.game.BallStatus;
import project12.group19.api.geometry.plane.PlanarCoordinate;

import java.util.Optional;

/**
 * Represents one of the game rounds. Please note that most of the
 * fields may be null for the ongoing round (use tryX() methods when
 * uncertain).
 */
public interface Round {
    /**
     * @return Sequential round number, 1-based.
     */
    int getIndex();

    /**
     * @return Ball position from which player has made a hit.
     */
    PlanarCoordinate getStartingPosition();

    /**
     * @return Ball position at round termination. Please note that
     * round termination may be not only ball stopping, but also
     */
    PlanarCoordinate getEndingPosition();

    /**
     * @return Hit made by the player.
     */
    Player.Hit getHit();

    /**
     * @return Status of the ball at the round termination.
     */
    BallStatus getTerminationReason();

    default Optional<PlanarCoordinate> tryGetEnd() {
        return Optional.ofNullable(getEndingPosition());
    }

    default Optional<Player.Hit> tryGetHit() {
        return Optional.ofNullable(getHit());
    }

    default Optional<BallStatus> tryGetTerminationReason() {
        return Optional.ofNullable(getTerminationReason());
    }

    default boolean isTerminated() {
        return getTerminationReason() != null;
    }

    record Standard(
            int index,
            PlanarCoordinate startingPosition,
            PlanarCoordinate endingPosition,
            Player.Hit hit,
            BallStatus terminationReason
    ) implements Round {
        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public PlanarCoordinate getStartingPosition() {
            return startingPosition;
        }

        @Override
        public PlanarCoordinate getEndingPosition() {
            return endingPosition;
        }

        @Override
        public Player.Hit getHit() {
            return hit;
        }

        @Override
        public BallStatus getTerminationReason() {
            return terminationReason;
        }
    }
}
