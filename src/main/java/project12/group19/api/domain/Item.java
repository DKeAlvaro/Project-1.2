package project12.group19.api.domain;

import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.geometry.plane.PlanarDimensions;
import project12.group19.api.geometry.plane.PlanarShape;
import project12.group19.api.physics.motion.Friction;

import java.util.Map;
import java.util.Objects;

/**
 * A very generic interface for representing a model in the simulation
 * this project acts upon.
 */
public interface Item extends PlanarShape {
    /**
     * Enumeration of possible actions that could be taken upon
     * discovery of collision with item.
     */
    enum CollisionReaction {
        NONE,
        RESET,
        REFLECT,
        SCORE
    }

    /**
     * @return A shape of the item.
     */
    PlanarShape getShape();

    /**
     * @return Action that should be taken upon collision of another
     * item with this one.
     */
    CollisionReaction getCollisionReaction();

    /**
     * @return Additional data attached to item that may be used, for
     * example, for rendering.
     */
    default Map<Object, Object> getMetadata() {
        return Map.of();
    }

    default boolean hasCollisionReaction(CollisionReaction reaction) {
        return Objects.equals(getCollisionReaction(), reaction);
    }

    @Override
    default PlanarDimensions getDimensions() {
        return getShape().getDimensions();
    }

    @Override
    default PlanarCoordinate getPosition() {
        return getShape().getPosition();
    }

    @Override
    default boolean includes(double x, double y) {
        return getShape().includes(x, y);
    }

    /**
     * An item present on the course the ball has to bounce off when
     * hit.
     *
     * @param shape Obstacle shape.
     * @param metadata Arbitrary attached properties.
     */
    record Obstacle(PlanarShape shape, Map<Object, Object> metadata) implements Item {
        @Override
        public PlanarShape getShape() {
            return shape;
        }

        @Override
        public CollisionReaction getCollisionReaction() {
            return CollisionReaction.REFLECT;
        }

        public static Obstacle create(PlanarShape shape, Map<Object, Object> metadata) {
            return new Obstacle(shape, metadata);
        }

        public static Obstacle create(PlanarShape shape) {
            return create(shape, Map.of());
        }
    }

    /**
     * Represents zones ball is not allowed to enter.
     *
     * @param shape Zone shape.
     * @param metadata Arbitrary attached properties.
     */
    record RestrictedZone(PlanarShape shape, Map<Object, Object> metadata) implements Item {
        @Override
        public PlanarShape getShape() {
            return shape;
        }

        @Override
        public CollisionReaction getCollisionReaction() {
            return CollisionReaction.RESET;
        }

        @Override
        public Map<Object, Object> getMetadata() {
            return metadata;
        }

        public static RestrictedZone create(PlanarShape shape, Map<Object, Object> metadata) {
            return new RestrictedZone(shape, metadata);
        }

        public static RestrictedZone create(PlanarShape shape) {
            return create(shape, Map.of());
        }
    }

    /**
     * An area over the surface that has different features rather than
     * the ground.
     *
     * @param shape Shape of the overlay.
     * @param friction Friction override, if present.
     * @param metadata Arbitrary attached data.
     */
    record Overlay(PlanarShape shape, Friction friction, Map<Object, Object> metadata) implements Item {
        @Override
        public PlanarShape getShape() {
            return shape;
        }

        @Override
        public CollisionReaction getCollisionReaction() {
            return CollisionReaction.NONE;
        }

        @Override
        public Map<Object, Object> getMetadata() {
            return metadata;
        }

        public static Overlay create(PlanarShape shape, Friction friction, Map<Object, Object> metadata) {
            return new Overlay(shape, friction, metadata);
        }

        public static Overlay create(PlanarShape shape, Friction friction) {
            return create(shape, friction, Map.of());
        }

        public static Overlay create(PlanarShape shape) {
            return create(shape, null, Map.of());
        }
    }

    /**
     * A hole to hit.
     *
     * @param shape A shape of the whole (which can be different because
     * of the position and radius).
     * @param metadata Arbitrary attached data.
     */
    record Target(PlanarShape shape, Map<Object, Object> metadata) implements Item {
        @Override
        public PlanarShape getShape() {
            return shape;
        }

        @Override
        public CollisionReaction getCollisionReaction() {
            return CollisionReaction.SCORE;
        }

        @Override
        public Map<Object, Object> getMetadata() {
            return metadata;
        }

        public static Target create(PlanarCoordinate coordinate, double radius, Map<Object, Object> metadata) {
            return new Target(PlanarShape.Ellipse.centered(coordinate, radius * 2, radius * 2), metadata);
        }

        public static Target create(PlanarCoordinate coordinate, double radius) {
            return create(coordinate, radius, Map.of());
        }

        public static Target create(double x, double y, double radius, Map<Object, Object> metadata) {
            return create(PlanarCoordinate.create(x, y), radius, metadata);
        }

        public static Target create(double x, double y, double radius) {
            return create(x, y, radius, Map.of());
        }
    }
}
