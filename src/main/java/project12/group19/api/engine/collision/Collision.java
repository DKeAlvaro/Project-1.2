package project12.group19.api.engine.collision;

import project12.group19.api.geometry.plane.PlanarCoordinate;

/**
 * A structure with information about collision: where did it happen and
 * what was the angle to the tangent line of surface during the
 * collision.
 */
public interface Collision {
    PlanarCoordinate getPosition();
    double getAngle();

    record Standard(PlanarCoordinate position, double angle) implements Collision {
        @Override
        public PlanarCoordinate getPosition() {
            return position;
        }

        @Override
        public double getAngle() {
            return angle;
        }
    }
}
