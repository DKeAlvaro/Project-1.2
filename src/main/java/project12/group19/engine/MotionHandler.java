package project12.group19.engine;

import project12.group19.api.domain.Course;
import project12.group19.api.domain.Item;
import project12.group19.api.game.BallStatus;
import project12.group19.api.geometry.plane.PlanarRectangle;
import project12.group19.api.motion.MotionCalculator;
import project12.group19.api.motion.MotionState;

public class MotionHandler {
    private final MotionCalculator calculator;

    public MotionHandler(MotionCalculator calculator) {
        this.calculator = calculator;
    }

    public Result next(Course course, MotionState current, double time, double interval) {
        MotionState raw = calculator.calculate(current, interval);
        double xDifference = raw.getXPosition() - current.getXPosition();
        double yDifference = raw.getYPosition() - current.getYPosition();
        // please note that xDifference may be zero, but double
        // precision floats take care of that
        // should xDifference be zero, the result would be Double.POSITIVE_INFINITY
        // or Double.NEGATIVE_INFINITY
        double slope = yDifference / xDifference;
        double x0 = raw.getXPosition() - raw.getYPosition() / slope;

        for (Item obstacle : course.getObstacles()) {
            // TODO: this implements a VERY lame collision approach
            // instead of calculating a line which it hits and mirroring
            // the direction relatively to that line it just inverts
            // only x or y, as if every obstacle was a parallelepiped
            // column
            // TODO: it also doesn't account for situations when the
            // previous and next positions are before and after the obstacle,
            // i.e. if the velocity is high enough and during the
            // simulation process there is no moment when ball position
            // actually falls within obstacle bounding, then obstacle
            // doesn't exist for the ball.
            PlanarRectangle bounds = PlanarRectangle.centered(
                    obstacle.getCoordinate(),
                    obstacle.getRadius() * 2,
                    obstacle.getRadius() * 2
            );

            if (bounds.contains(raw.getXPosition(), raw.getYPosition())) {
                break;
            }
        }
    }
    
    private static

    record Result(BallStatus status, MotionState state) {}
}
