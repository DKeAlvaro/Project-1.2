package project12.group19.engine.motion;

import project12.group19.api.domain.Course;
import project12.group19.api.game.BallStatus;
import project12.group19.api.game.Rules;
import project12.group19.api.physics.motion.*;
import project12.group19.physics.motion.StopCondition;

public class StandardMotionHandler implements MotionHandler {
    private final Course course;
    private final Rules rules;
    private final MotionCalculator calculator;

    public StandardMotionHandler(Course course, Rules rules, MotionCalculator calculator) {
        this.course = course;
        this.rules = rules;
        this.calculator = calculator;
    }

    @Override
    public MotionResult next(MotionState state, double deltaT) {
        MotionState calculated = calculator.calculate(state, deltaT);

        if (!rules.getFieldBoundaries().includes(calculated.getPosition())) {
            return MotionResult.create(calculated, BallStatus.ESCAPED);
        }

        if (course.getTarget().includes(calculated.getPosition())) {
            return MotionResult.create(calculated, BallStatus.SCORED);
        }

        if (course.getSurface().getHeight(calculated.getPosition()) < 0) {
            return MotionResult.create(calculated, BallStatus.DROWNED);
        }

        if (course.getRestrictedZones().anyMatch(zone -> zone.includes(calculated.getPosition()))) {
            return MotionResult.create(calculated, BallStatus.DROWNED);
        }

        if (StopCondition.isMoving(course.getSurface(), calculated, deltaT)) {
            return MotionResult.create(calculated, BallStatus.MOVING);
        }

        return MotionResult.create(calculated, BallStatus.STOPPED);
    }
}
