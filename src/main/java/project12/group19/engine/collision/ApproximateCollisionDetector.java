package project12.group19.engine.collision;

import project12.group19.api.domain.Item;
import project12.group19.api.engine.collision.Collision;
import project12.group19.api.engine.collision.CollisionDetector;
import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.geometry.plane.PlanarLineInterval;

import java.util.Map;
import java.util.Optional;

public class ApproximateCollisionDetector implements CollisionDetector {
    private final Map<String, Double> sizes;

    @Override
    public Optional<Collision> detect(PlanarLineInterval movement, double radius, Item item) {
        double size = sizes.getOrDefault(item.getType(), 0.0) + radius;


        return Optional.empty();
    }
}
