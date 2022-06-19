package project12.group19.api.domain;

import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.physics.Velocity;

public interface HitSimulation extends Velocity {
    PlanarCoordinate getStartingPoint();
    PlanarCoordinate getEndingPoint();
    double getMinimumDistance();

    record Standard(
            double xVelocity,
            double yVelocity,
            PlanarCoordinate startingPoint,
            PlanarCoordinate endingPoint,
            double minimumDistance
    ) implements HitSimulation {
        @Override
        public PlanarCoordinate getStartingPoint() {
            return startingPoint;
        }

        @Override
        public PlanarCoordinate getEndingPoint() {
            return endingPoint;
        }

        @Override
        public double getMinimumDistance() {
            return minimumDistance;
        }

        @Override
        public double getXVelocity() {
            return xVelocity;
        }

        @Override
        public double getYVelocity() {
            return yVelocity;
        }
    }
}
