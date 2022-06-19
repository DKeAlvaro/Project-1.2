package project12.group19.api.game.lifecycle;

import project12.group19.api.game.BallStatus;
import project12.group19.api.game.state.Round;

import java.util.List;
import java.util.Objects;

/**
 * TODO: Add support for elapsed time and ticks
 */
public interface GameStats {
    List<Round> getRounds();
    default int getFouls() {
        return (int) getRounds().stream()
                .map(Round::getTerminationReason)
                .filter(Objects::nonNull)
                .filter(BallStatus::isFoulTrigger)
                .count();
    }
    default boolean isWon() {
        List<Round> rounds = getRounds();
        return !rounds.isEmpty() && rounds.get(rounds.size() - 1).getTerminationReason().equals(BallStatus.SCORED);
    }
    boolean hasEndedExceptionally();

    record Standard(List<Round> rounds, boolean endedExceptionally) implements GameStats {
        @Override
        public List<Round> getRounds() {
            return rounds;
        }

        @Override
        public boolean hasEndedExceptionally() {
            return endedExceptionally;
        }
    }
}
