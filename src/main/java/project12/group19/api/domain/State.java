package project12.group19.api.domain;

import project12.group19.api.motion.MotionState;

/**
 * Contains current game state.
 */
public interface State {
    Course getCourse();
    MotionState getBallState();
    boolean isTerminal();
    boolean isStatic();
    int getHits();
    int getFouls();

    record Standard(
            Course course,
            MotionState ballState,
            boolean isTerminal,
            boolean isStatic,
            int hits,
            int fouls
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
        public boolean isTerminal() {
            return isTerminal;
        }

        @Override
        public boolean isStatic() {
            return isStatic;
        }

        @Override
        public int getHits() {
            return hits;
        }

        @Override
        public int getFouls() {
            return fouls;
        }
    }
}
