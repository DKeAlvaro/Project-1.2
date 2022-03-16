package project12.group19.api.domain;

import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.geometry.space.Hole;

import java.util.Set;

/**
 * Describes the played golf course.
 */
public interface Course {
    HeightProfile getSurface();
    Item getBall();
    Set<Item> getObstacles();
    Hole getHole();

    record Standard(
            HeightProfile surface,
            Item ball,
            Set<Item> obstacles,
            Hole hole
    ) implements Course {
        @Override
        public HeightProfile getSurface() {
            return surface;
        }

        @Override
        public Item getBall() {
            return ball;
        }

        @Override
        public Set<Item> getObstacles() {
            return obstacles;
        }

        @Override
        public Hole getHole() {
            return hole;
        }
    }
}
