package project12.group19.api.game.path;

import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.geometry.plane.PlanarRectangle;
import project12.group19.api.geometry.plane.PlanarShape;

/**
 * Wayspot is an anchor area on map that can be used as an intermediate
 * point on the way from starting point to the end, an area that the
 * ball may visit during its path. It is expected that pathfinding
 * infrastructure would first define wayspots on map and then examine
 * the opportunity of connecting them with hits to make a path from one
 * point to another.
 */
public interface WaySpot {
    /**
     * @return The best point algorithms should strive to put the ball
     * onto.
     */
    PlanarCoordinate getDefiningPoint();

    /**
     * @return A shape of wayspot area. It is expected that the actual
     * wayspot shape isn't important, what's important is whether a
     * point belongs to wayspot or not - and for that one can use
     * {@link PlanarShape#includes(PlanarCoordinate)} method.
     */
    PlanarShape getShape();

    record Rectangular(PlanarRectangle definingBox, PlanarCoordinate definingPoint) implements WaySpot {
        public Rectangular(PlanarRectangle definition) {
            this(definition, definition.getCenter());
        }

        @Override
        public PlanarCoordinate getDefiningPoint() {
            return definingPoint;
        }

        @Override
        public PlanarShape getShape() {
            return definingBox;
        }
    }

    record Point(PlanarShape.Point definition) implements WaySpot {
        public Point(PlanarCoordinate coordinate) {
            this(new PlanarShape.Point(coordinate));
        }

        @Override
        public PlanarCoordinate getDefiningPoint() {
            return definition.coordinate();
        }

        @Override
        public PlanarShape getShape() {
            return definition;
        }
    }
}
