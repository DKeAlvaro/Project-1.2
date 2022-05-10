package project12.group19.engine;

import project12.group19.api.domain.Item;
import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.geometry.plane.PlanarDimensions;
import project12.group19.api.geometry.plane.PlanarRectangle;

import java.util.Map;

/**
 * Collision detector responds to a very simple query: whether the object
 *
 */
public interface CollisionDetector {
    record Movement(Item subject, PlanarCoordinate source, PlanarCoordinate target) {}

    boolean collides(Movement a, Movement b);

    class BoundingBoxCollisionDetector implements CollisionDetector {
        private static final PlanarDimensions FALLBACK_DIMENSIONS = PlanarDimensions.square(1);

        private final Map<String, PlanarDimensions> baseDimensions;

        public BoundingBoxCollisionDetector(Map<String, PlanarDimensions> baseDimensions) {
            this.baseDimensions = baseDimensions;
        }

        public BoundingBoxCollisionDetector() {
            this(Map.of());
        }

        private PlanarDimensions dimensionsOf(Item obstacle) {
            return baseDimensions.getOrDefault(obstacle.getType(), FALLBACK_DIMENSIONS).scale(obstacle.getSize());
        }

        private static boolean crosses(double source, double target, double value) {
            return (source <= value && value <= target) || (source >= value && value >= target);
        }

        private static boolean crosses(double source, double target, double intervalStart, double intervalEnd) {
            return crosses(source, target, intervalStart) || crosses(source, target, intervalEnd);
        }

        @Override
        public boolean collides(PlanarCoordinate source, PlanarCoordinate target, Item obstacle) {
            PlanarRectangle box = PlanarRectangle.create(obstacle.getCoordinate(), dimensionsOf(obstacle));

            boolean crossesX = crosses(source.getX(), target.getX(), box.getX(), box.getX() + box.getWidth());
            boolean crossesY = crosses(source.getY(), target.getY(), box.getY(), box.getY() + box.getHeight());

            return crossesX && crossesY;
        }
    }
}
