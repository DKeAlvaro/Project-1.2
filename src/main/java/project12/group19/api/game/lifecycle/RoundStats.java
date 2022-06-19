package project12.group19.api.game.lifecycle;

import project12.group19.api.game.BallStatus;
import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.physics.Velocity;

import java.util.Map;

public interface RoundStats {
    long getElapsedTime();
    long getElapsedTicks();
    PlanarCoordinate getStartingPoint();
    PlanarCoordinate getEndingPoint();
    Velocity getRequestedHit();
    Velocity getAppliedHit();
    BallStatus getResult();
    Map<Object, Object> getMetadata();

    record Standard(
            long elapsedTime,
            long ticks,
            PlanarCoordinate startingPoint,
            PlanarCoordinate endingPoint,
            Velocity requestedHit,
            Velocity appliedHit,
            BallStatus result,
            Map<Object, Object> metadata
    ) implements RoundStats {
        @Override
        public long getElapsedTime() {
            return elapsedTime;
        }

        @Override
        public long getElapsedTicks() {
            return ticks;
        }

        @Override
        public PlanarCoordinate getStartingPoint() {
            return startingPoint;
        }

        @Override
        public PlanarCoordinate getEndingPoint() {
            return endingPoint;
        }

        @Override
        public Velocity getRequestedHit() {
            return requestedHit;
        }

        @Override
        public Velocity getAppliedHit() {
            return appliedHit;
        }

        @Override
        public BallStatus getResult() {
            return result;
        }

        @Override
        public Map<Object, Object> getMetadata() {
            return metadata;
        }
    }
}
