package project12.group19.player.ai;

import project12.group19.api.domain.Player;
import project12.group19.api.domain.State;
import project12.group19.api.game.Configuration;
import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.motion.Solver;
//import project12.group19.incubating.HillClimbing1;
import project12.group19.incubating.HillClimbing2;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Implementations of this interface calculate x-y velocity pair
 * required to be applied to ball to end up in selected target.
 *
 * Since bot is expected to be capable of doing several hits to reach
 * target in sophisticated way, provided argument is not the hole
 * itself, but a some position in the field.
 *
 * If calculator can't compute good enough result, it should return
 * empty optional (which can be an expected result on some complex
 * surfaces). Returning such a response is not a failure, but an
 * expected outcome. Calculator should try to compute a score as close
 * as possible to the target parameter, but everything that satisfies
 * passed tolerance is a valid return value.
 */
public interface HitCalculator {
    /**
     * Projects a hit required for ball to stop at target position.
     *
     * @param state Current game state.
     * @param target Point to stop at.
     * @param tolerance Distance from the point that is counted as ok.
     * @return A hit that is necessary to end up at target.
     */
    Optional<Player.Hit> shootAt(State state, PlanarCoordinate target, double tolerance);
    /**
     * Projects a hit required for ball to pass within a specified
     * distance of target. This is a relaxed condition compared to
     * {@link #shootAt(State, PlanarCoordinate, double)}, since it
     * doesn't require precise force calculation or stopping at a
     * possibly steep point.
     *
     * @param state Current game state.
     * @param target Point to shoot through.
     * @param tolerance Distance from the point that is counted as ok.
     * @return A hit that would put the ball through the target within
     * specified tolerance.
     */
    default Optional<Player.Hit> shootThrough(State state, PlanarCoordinate target, double tolerance) {
        return shootAt(state, target, tolerance);
    }

    class Predefined implements HitCalculator {
        private final List<Player.Hit> hits;

        private int counter;

        public Predefined(List<Player.Hit> hits) {
            this.hits = hits;
        }

        @Override
        public Optional<Player.Hit> shootAt(State state, PlanarCoordinate target, double tolerance) {
            if (hits.isEmpty()) {
                return Optional.empty();
            }

            Player.Hit next = hits.get(counter);
            counter = (counter + 1) % hits.size();

            return Optional.of(next);
        }
    }

    class Directed implements HitCalculator {
        private static final double NOISE_PERCENTAGE = 0;
        private static final Random NOISE = new Random();
        private static Configuration configuration;
        private final Solver solver;

        public Directed(Solver solver, Configuration configuration) {
            this.solver = solver;
            this.configuration = configuration;

        }

        @Override
        public Optional<Player.Hit> shootAt(State state, PlanarCoordinate target, double tolerance) {
            /*
            MotionState ball = state.getBallState();
            Hole hole = state.getCourse().getHole();
            double xPath = hole.getxHole() - ball.getXPosition();
            double yPath = hole.getyHole() - ball.getYPosition();
            double angle = Math.atan2(yPath, xPath);
            double xVelocity = noisify(Math.cos(angle) * force);
            double yVelocity = noisify(Math.sin(angle) * force);
            return Optional.of(Player.Hit.create(xVelocity, yVelocity));
            */

            HillClimbing2 hillClimbing2 = new HillClimbing2(solver, configuration);
            return hillClimbing2.hillClimbing1(state.getBallState().getXPosition(), state.getBallState().getYPosition());
        }


        private static double noisify(double value) {
            double multiplier = 1 - NOISE_PERCENTAGE * (2 * NOISE.nextDouble() - 1);
            return value * multiplier;
        }
    }
}