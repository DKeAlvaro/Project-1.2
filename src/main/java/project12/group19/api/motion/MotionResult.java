package project12.group19.api.motion;

import project12.group19.api.game.BallStatus;

public interface MotionResult {
    MotionState getState();
    BallStatus getStatus();

    static MotionResult create(MotionState state, BallStatus status) {
        return new Standard(state, status);
    }

    record Standard(MotionState state, BallStatus status) implements MotionResult {
        @Override
        public BallStatus getStatus() {
            return status;
        }

        @Override
        public MotionState getState() {
            return state;
        }
    }
}
