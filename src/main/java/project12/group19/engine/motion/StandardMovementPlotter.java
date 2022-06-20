package project12.group19.engine.motion;

import project12.group19.api.game.BallStatus;
import project12.group19.api.physics.motion.MotionHandler;
import project12.group19.api.physics.motion.MotionResult;
import project12.group19.api.physics.motion.MotionState;
import project12.group19.api.physics.motion.MovementPlotter;

import java.util.function.BiFunction;

public class StandardMovementPlotter implements MovementPlotter {
    private final MotionHandler handler;

    public StandardMovementPlotter(MotionHandler handler) {
        this.handler = handler;
    }

    @Override
    public <T> T calculate(MotionState state, double step, BiFunction<T, MotionResult, T> reducer, T initial) {
        MotionResult motion = MotionResult.create(state, BallStatus.MOVING);
        T outcome = initial;

        while (motion.getStatus().equals(BallStatus.MOVING)) {
            motion = handler.next(motion.getState(), step);
            outcome = reducer.apply(outcome, motion);
        }

        return outcome;
    }
}
