package project12.group19.api.game.configuration;

import java.util.Optional;
import java.util.OptionalDouble;

public interface EngineConfiguration {
    Timing getTiming();
    Noise getNoise();
    Physics getPhysics();

    default EngineConfiguration withTiming(Timing timing) {
        return new Standard(timing, getNoise(), getPhysics());
    }

    default EngineConfiguration withNoise(Noise noise) {
        return new Standard(getTiming(), noise, getPhysics());
    }

    default EngineConfiguration withPhysics(Physics physics) {
        return new Standard(getTiming(), getNoise(), physics);
    }

    record Standard(Timing timing, Noise noise, Physics physics) implements EngineConfiguration {
        @Override
        public Timing getTiming() {
            return timing;
        }

        @Override
        public Noise getNoise() {
            return noise;
        }

        @Override
        public Physics getPhysics() {
            return physics;
        }
    }

    interface Timing {
        /**
         * @return Time step to be used between two states.
         */
        double getTimeStep();

        /**
         * Number of seconds to wait between computing two states.
         * By default, this is computed as {@link #getTimeStep()}.
         */
        OptionalDouble getComputationalInterval();

        /**
         * @return Number of seconds between two listener
         * notifications. Notifications are done as a part of
         * computations and because of that that interval may be in fact
         * approximated. By default {@link #getComputationalInterval()}
         * value is used.
         */
        OptionalDouble getNotificationInterval();

        default double resolveComputationalInterval() {
            return getComputationalInterval().orElse(getTimeStep());
        }


        default double resolveNotificationInterval() {
            return getNotificationInterval().orElse(resolveComputationalInterval());
        }

        record Standard(double timeStep, OptionalDouble computationalInterval, OptionalDouble notificationInterval) implements Timing {
            @Override
            public double getTimeStep() {
                return timeStep;
            }

            @Override
            public OptionalDouble getComputationalInterval() {
                return computationalInterval;
            }

            @Override
            public OptionalDouble getNotificationInterval() {
                return notificationInterval;
            }
        }
    }

    static EngineConfiguration defaults() {
        return new Standard(
                new Timing.Standard(0.01, OptionalDouble.empty(), OptionalDouble.empty()),
                Noise.empty(),
                Physics.empty()
        );
    }

    interface Noise {
        /**
         * @return The range in which velocity of the hit may be altered.
         */
        OptionalDouble getVelocityRange();

        /**
         * @return The range in which angle can be diverted (in pi, so
         * 1.0 would correspond to range of [-pi, +pi]).
         */
        OptionalDouble getDirectionRange();

        static Noise empty() {
            return new Standard(OptionalDouble.empty(), OptionalDouble.empty());
        }

        record Standard(OptionalDouble velocityRange, OptionalDouble directionRange) implements Noise {
            @Override
            public OptionalDouble getVelocityRange() {
                return velocityRange;
            }

            @Override
            public OptionalDouble getDirectionRange() {
                return directionRange;
            }
        }
    }

    interface Physics {
        Optional<String> getOdeSolver();
        Optional<String> getAccelerationCalculator();

        static Physics empty() {
            return new Standard(null, null);
        }

        record Standard(String odeSolver, String accelerationCalculator) implements Physics {
            @Override
            public Optional<String> getOdeSolver() {
                return Optional.ofNullable(odeSolver);
            }

            @Override
            public Optional<String> getAccelerationCalculator() {
                return Optional.ofNullable(accelerationCalculator);
            }
        }
    }
}
