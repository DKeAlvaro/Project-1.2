package project12.group19.player.ai;

import project12.group19.api.domain.Hit;
import project12.group19.api.domain.State;
import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.motion.MotionState;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

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
    Optional<Hit> shootAt(State state, PlanarCoordinate target, double tolerance) throws FileNotFoundException;
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
    default Optional<Hit> shootThrough(State state, PlanarCoordinate target, double tolerance) throws FileNotFoundException {
        return shootAt(state, target, tolerance);
    }

    class Predefined implements HitCalculator {
        private final List<Hit> hits;

        private int counter;

        public Predefined(List<Hit> hits) {
            this.hits = hits;
        }

        @Override
        public Optional<Hit> shootAt(State state, PlanarCoordinate target, double tolerance) {
            if (hits.isEmpty()) {
                return Optional.empty();
            }

            Hit next = hits.get(counter);
            counter = (counter + 1) % hits.size();

            return Optional.of(next);
        }
    }

    class Adjusting implements HitCalculator {
        private final double start;
        private final double step;

        private int counter;

        public Adjusting(double start, double step) {
            this.start = start;
            this.step = step;
        }

        public Adjusting() {
            this(1.0, 0.2);
        }

        @Override
        public Optional<Hit> shootAt(State state, PlanarCoordinate target, double tolerance) {
            MotionState ball = state.getBallState();
            double force = start * Math.pow(1 + step, counter);
            counter++;
            double xPath = target.getX() - ball.getXPosition();
            double yPath = target.getY() - ball.getYPosition();
            double angle = Math.atan2(yPath, xPath);
            double xVelocity = Math.cos(angle) * force;
            double yVelocity = Math.sin(angle) * force;
            return Optional.of(Hit.create(xVelocity, yVelocity));
        }
    }
}
