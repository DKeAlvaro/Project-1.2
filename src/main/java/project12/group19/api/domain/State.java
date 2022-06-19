package project12.group19.api.domain;

import project12.group19.api.game.BallStatus;
import project12.group19.api.game.Rules;
import project12.group19.api.game.state.GameStatus;
import project12.group19.api.game.state.Round;
import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.motion.MotionState;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.UnaryOperator;

/**
 * Contains current game state.
 */
public interface State {
    List<Round> getRounds();
    Course getCourse();
    MotionState getBallState();
    Rules getRules();
    default GameStatus getGameStatus() {
        return getGameStatus(getRounds(), getRules());
    }
    default boolean isTerminal() {
        return isTerminal(getRounds(), getRules());
    }
    default boolean isStatic() {
        return isStatic(getRounds());
    }
    default int getHits() {
        return getHits(getRounds());
    }
    default int getFouls() {
        return getFouls(getRounds());
    }

    default Round getLastRound() {
        List<Round> rounds = getRounds();
        return rounds.get(rounds.size() - 1);
    }

    default State updateLastRound(UnaryOperator<Round> transformer) {
        List<Round> rounds = new ArrayList<>(getRounds());
        Round last = getLastRound(rounds);
        rounds.remove(rounds.size() - 1);
        rounds.add(transformer.apply(last));
        return new Standard(
                rounds,
                getCourse(),
                getBallState(),
                getRules()
        );
    }

    default State terminateRound(PlanarCoordinate coordinate, BallStatus status) {
        return updateLastRound(round -> new Round.Standard(round.getIndex(), round.getStartingPosition(), coordinate, round.getHit(), status));
    }

    default State nextRound(PlanarCoordinate position) {
        if (isTerminal()) {
            return this;
        }

        List<Round> rounds = new ArrayList<>(getRounds());
        rounds.add(new Round.Standard(
                rounds.size() + 1,
                position,
                null,
                null,
                null
        ));
        MotionState ballState = new MotionState.Standard(
                0,
                0,
                position.getX(),
                position.getY()
        );
        return new Standard(rounds, getCourse(), ballState, getRules());
    }

    default State withBallState(MotionState state) {
        return new Standard(getRounds(), getCourse(), state, getRules());
    }

    default State withHit(Hit hit) {
        return new Standard(
                mapLastRound(getRounds(), round -> new Round.Standard(round.getIndex(), round.getStartingPosition(), null, hit, null)),
                getCourse(),
                hit.apply(getBallState()),
                getRules()
        );
    }

    static State initial(MotionState ballState, Course course, Rules rules) {
        return new Standard(
                List.of(new Round.Standard(1, ballState.getPosition(), null, null, null)),
                course,
                ballState,
                rules
        );
    }

    static int getHits(List<Round> rounds) {
        int hits = 0;
        for (Round round : rounds) {
            if (round.getHit() != null) {
                hits++;
            }
        }
        return hits;
    }

    static int getFouls(List<Round> rounds) {
        int fouls = 0;
        for (Round round : rounds) {
            if (BallStatus.ESCAPED.equals(round.getTerminationReason()) || BallStatus.DROWNED.equals(round.getTerminationReason())) {
                fouls++;
            }
        }
        return fouls;
    }

    static Round getLastRound(List<Round> rounds) {
        return rounds.get(rounds.size() - 1);
    }

    static GameStatus getGameStatus(List<Round> rounds, Rules rules) {
        if (BallStatus.SCORED.equals(getLastRound(rounds).getTerminationReason())) {
            return GameStatus.WON;
        }

        OptionalInt allowedFouls = rules.getAllowedFouls();
        if (allowedFouls.isPresent() && allowedFouls.getAsInt() < getFouls(rounds)) {
            return GameStatus.LOST;
        }

        return GameStatus.ONGOING;
    }

    static boolean isTerminal(List<Round> rounds, Rules rules) {
        return !GameStatus.ONGOING.equals(getGameStatus(rounds, rules));
    }
    static boolean isStatic(List<Round> rounds) {
        return getLastRound(rounds).getTerminationReason() == null;
    }

    static List<Round> mapLastRound(List<Round> rounds, UnaryOperator<Round> transformer) {
        Round replacement = transformer.apply(getLastRound(rounds));
        List<Round> updated = new ArrayList<>(rounds);
        updated.remove(updated.size() - 1);
        updated.add(replacement);
        return updated;
    }

    record Standard(
            List<Round> rounds,
            Course course,
            MotionState ballState,
            Rules rules
    ) implements State {
        @Override
        public Course getCourse() {
            return course;
        }

        @Override
        public MotionState getBallState() {
            return ballState;
        }

        @Override
        public List<Round> getRounds() {
            return rounds;
        }

        @Override
        public Rules getRules() {
            return rules;
        }
    }
}
