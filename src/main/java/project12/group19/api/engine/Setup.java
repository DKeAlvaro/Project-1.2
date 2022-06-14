package project12.group19.api.engine;

import project12.group19.api.domain.Course;
import project12.group19.api.domain.Player;
import project12.group19.api.domain.State;
import project12.group19.api.game.Configuration;
import project12.group19.api.game.HitMutator;
import project12.group19.api.game.Rules;
import project12.group19.api.motion.MotionHandler;

import java.util.List;
import java.util.function.Consumer;

public interface Setup {
    Configuration getConfiguration();

    /**
     * @return Number of times per second engine calculates game state.
     * Bigger values result in improved accuracy and increased CPU
     * consumption.
     */
    int getDesiredTickRate();

    /**
     * @return Number of times per second UI should be refreshed. May be
     * bounded by sane values (e.g. max 60Hz).
     */
    int getDesiredRefreshRate();

    /**
     * @return Calculator used to update ball position.
     */
    MotionHandler getMotionHandler();
    Player getPlayer();

    /**
     * @return An optional list of listeners which will be notified on
     * each tick.
     */
    List<Consumer<State>> getListeners();
    Course getCourse();
    Rules getRules();
    HitMutator getHitMutator();

    record Standard(
            Configuration configuration,
            Course course,
            Rules rules,
            int desiredTickRate,
            int desiredRefreshRate,
            MotionHandler motionHandler,
            Player player,
            HitMutator hitMutator,
            List<Consumer<State>> listeners
    ) implements Setup {
        @Override
        public Configuration getConfiguration() {
            return configuration;
        }

        @Override
        public int getDesiredTickRate() {
            return desiredTickRate;
        }

        @Override
        public int getDesiredRefreshRate() {
            return desiredRefreshRate;
        }

        @Override
        public MotionHandler getMotionHandler() {
            return motionHandler;
        }

        @Override
        public Player getPlayer() {
            return player;
        }

        @Override
        public List<Consumer<State>> getListeners() {
            return listeners;
        }

        @Override
        public Course getCourse() {
            return course;
        }

        @Override
        public Rules getRules() {
            return rules;
        }

        @Override
        public HitMutator getHitMutator() {
            return hitMutator;
        }
    }
}
