package project12.group19.api.engine.collision;

import project12.group19.api.domain.Item;
import project12.group19.api.geometry.plane.PlanarLineInterval;

import java.util.Optional;

/**
 * Interface for a very collision detector. Given a moving round item
 * and another stationary item in XY plane, finds a place and angle of
 * collision, if any.
 */
public interface CollisionDetector {
    Optional<Collision> detect(PlanarLineInterval movement, double radius, Item item);
}
