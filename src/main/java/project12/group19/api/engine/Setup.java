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

    Timing getTiming();

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

    interface Timing {
        double getStep();

        /**
         * @return Time between two computations, in nanoseconds
         */
        long getComputationInterval();

        /**
         * @return time between listeners are notified, in nanoseconds
         */
        long getNotificationInterval();

        record Standard(double step, long computationInterval, long notificationInterval) implements Timing {
            @Override
            public double getStep() {
                return step;
            }

            @Override
            public long getComputationInterval() {
                return computationInterval;
            }

            @Override
            public long getNotificationInterval() {
                return notificationInterval;
            }
        }
    }

    record Standard(
            Configuration configuration,
            Course course,
            Rules rules,
            Timing timing,
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
        public Timing getTiming() {
            return timing;
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
