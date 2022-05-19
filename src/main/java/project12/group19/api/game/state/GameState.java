package project12.group19.api.game.state;

import project12.group19.api.game.BallStatus;
import project12.group19.api.game.Rules;
import project12.group19.api.geometry.plane.PlanarCoordinate;

import java.util.List;
import java.util.Optional;

public interface GameState {
    GameStatus getStatus();
    List<Round> getRounds();
    default List<Round> getFinishedRounds() {
        return getRounds().stream().filter(round -> round.getTerminationReason() != null).toList();
    }
    default Optional<Round> getCurrentRound() {
        List<Round> rounds = getRounds();
        return rounds.isEmpty() ? Optional.empty() : Optional.of(rounds.get(rounds.size() - 1));
    }
    default int getFouls() {
        int fouls = 0;
        for (Round round : getRounds()) {
            if (BallStatus.ESCAPED.equals(round.getTerminationReason()) || BallStatus.DROWNED.equals(round.getTerminationReason())) {
                fouls++;
            }
        }
        return fouls;
    }
    PlanarCoordinate getBallPosition();
    Rules getRules();
}
