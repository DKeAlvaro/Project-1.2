package project12.group19.api.domain;

import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.physics.Velocity;

public interface HitSimulation extends Velocity {
    PlanarCoordinate getStartingPoint();
    PlanarCoordinate getEndingPoint();
    long getCalculationTime();
    long getMinimumDistance();
    long getFinalDistance();

    record Standard(
            double xVelocity,
            double yVelocity,
            PlanarCoordinate startingPoint,
            PlanarCoordinate endingPoint,
            long calculationTime,
            long minimumDistance,
            long finalDistance
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
        public long getCalculationTime() {
            return calculationTime;
        }

        @Override
        public long getMinimumDistance() {
            return minimumDistance;
        }

        @Override
        public long getFinalDistance() {
            return finalDistance;
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
