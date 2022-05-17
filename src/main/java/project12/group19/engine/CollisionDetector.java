package project12.group19.engine;

import project12.group19.api.domain.Item;
import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.geometry.plane.PlanarDimensions;
import project12.group19.api.geometry.plane.PlanarRectangle;
import project12.group19.geometry.Line;

import java.util.Map;
import java.util.Optional;

/**
 * Collision detector responds to a very simple query: whether the
 * moving object collides with a static one during its movement.
 *
 * Due to the nature of the project, following decisions were made:
 * - The second object is always static.
 * - The movement is strictly linear in respect to XY-coordinates.
 */
public interface CollisionDetector {
    interface Collision {
        /**
         * @return A point where two objects collide.
         */
        PlanarCoordinate getCoordinate();

        /**
         * @return Tangent line to static object at the point of
         * collision.
         */
        Line getTangent();
    }
    Optional<Collision> detect(Item subject, PlanarCoordinate source, PlanarCoordinate target, Item obstacle);

    /**
     * A placeholder, doesn't detect anything at all.
     */
    class NoOpCollisionDetector implements CollisionDetector {
        @Override
        public Optional<Collision> detect(Item subject, PlanarCoordinate source, PlanarCoordinate target, Item obstacle) {
            return Optional.empty();
        }
    }

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
        public Optional<Collision> detect(Item subject, PlanarCoordinate source, PlanarCoordinate target, Item obstacle) {
            // For a collision, the outer box of one item (ball) touches
            // the outer box of another item (obstacle) through its
            // movement.
            // That can be detected by creating a movement polygon and
            // checking whether it overlaps with polygon of outer box of
            // the second item, but it can be simplified by "transferring"
            // the outer dimensions of moving item to the static one, and
            // then check only whether the line of movement perpetrates
            // the extended static zone
            PlanarDimensions extension = dimensionsOf(subject).scale(1 / 2.0);
            PlanarRectangle box = PlanarRectangle.create(obstacle.getCoordinate(), dimensionsOf(obstacle).add(extension));

            boolean crossesX = crosses(source.getX(), target.getX(), box.getX(), box.getX() + box.getWidth());
            boolean crossesY = crosses(source.getY(), target.getY(), box.getY(), box.getY() + box.getHeight());

            // TODO: work abandoned for switching to more important things

            return Optional.empty();
        }
    }
}
