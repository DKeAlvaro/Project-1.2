package project12.group19.api.domain;

import project12.group19.api.geometry.space.Hole;
import project12.group19.api.motion.Friction;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Describes the played golf course.
 */
public interface Course {
    /**
     * @return Object providing access to the features of surface at
     * specific point.
     */
    Surface getSurface();

    /**
     * @deprecated use {@link Surface#getFriction(double, double)}
     */
    @Deprecated
    Friction getSurfaceFriction();

    /**
     * @return Additional items present on course.
     */
    Set<Item> getItems();

    default Stream<Item> streamItems(Predicate<Item> filter) {
        return getItems().stream().filter(filter);
    }

    default Stream<Item> streamItems(Item.CollisionReaction reaction) {
        return streamItems(item -> item.hasCollisionReaction(reaction));
    }

    default <T extends Item> Stream<T> streamItems(Class<T> type) {
        return streamItems(item -> type.isAssignableFrom(item.getClass()))
                .map(type::cast);
    }

    default Stream<Item.RestrictedZone> getRestrictedZones() {
        return streamItems(Item.RestrictedZone.class);
    }

    default Stream<Item.Overlay> getOverlays() {
        return streamItems(Item.Overlay.class);
    }

    default Stream<Item.Obstacle> getObstacles() {
        return streamItems(Item.Obstacle.class);
    }

    default Item getTarget() {
        return streamItems(Item.CollisionReaction.SCORE)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Target is not present among course items"));
    }

    /**
     * @deprecated use {@link #getTarget()}
     */
    @Deprecated
    default Hole getHole() {
        Item target = getTarget();
        return new Hole(target.getCenter().getX(), target.getCenter().getY(), target.getWidth());
    }

    record Standard(
            Surface surface,
            Friction surfaceFriction,
            Set<Item> items
    ) implements Course {
        @Override
        public Surface getSurface() {
            return surface;
        }

        @Override
        public Friction getSurfaceFriction() {
            return surfaceFriction;
        }

        @Override
        public Set<Item> getItems() {
            return items;
        }
    }
}
