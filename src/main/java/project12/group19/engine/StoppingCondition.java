package project12.group19.engine;

import project12.group19.api.domain.Course;
import project12.group19.api.motion.MotionState;

/**
 * Tells whether
 */
public interface StoppingCondition {
    boolean isStopped(MotionState state, Course course);
}
