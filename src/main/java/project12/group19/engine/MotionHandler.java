package project12.group19.engine;

import project12.group19.api.domain.Course;
import project12.group19.api.game.lifecycle.BallStatus;
import project12.group19.api.motion.MotionCalculator;
import project12.group19.api.motion.MotionState;

public class MotionHandler {
    private final MotionCalculator calculator;
    private final CollisionDetector collisionDetector;
    private final StoppingCondition condition;

    public MotionHandler(MotionCalculator calculator, CollisionDetector collisionDetector, StoppingCondition condition) {
        this.calculator = calculator;
        this.collisionDetector = collisionDetector;
        this.condition = condition;
    }

    public Result next(MotionState current, Course course, double timestamp, double interval) {
        MotionState prediction = calculator.calculate(current, interval);

        // TODO: collision detection and handling

        // TODO: check whether ball crossed a lake during movement

        if (course.getSurface().getHeight(prediction.getPosition()) < 0) {
            return new Result(BallStatus.DROWNED, prediction);
        }

        if (condition.isStopped(prediction, course)) {
            return new Result(BallStatus.STOPPED, prediction);
        }

        return new Result(BallStatus.MOVING, prediction);
    }

    record Result(BallStatus status, MotionState state) {}
}
