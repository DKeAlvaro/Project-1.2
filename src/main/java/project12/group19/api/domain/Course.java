package project12.group19.api.domain;

import project12.group19.api.geometry.space.HeightProfile;

import java.util.Set;

/**
 * Describes the played golf course.
 */
public interface Course {
    HeightProfile getSurface();
    Item getBall();
    Set<Item> getObstacles();

    record Standard(
            HeightProfile surface,
            Item ball,
            Set<Item> obstacles
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
    }
}
