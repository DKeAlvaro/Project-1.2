package project12.group19.api.domain;

import project12.group19.api.geometry.plane.PlanarRectangle;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.geometry.space.Hole;
import project12.group19.api.motion.Friction;

import java.util.Set;

/**
 * Describes the played golf course.
 *
 * TODO: convert lakes to something more interesting than rectangles.
 */
public interface Course {
    HeightProfile getSurface();
    Friction getSurfaceFriction();
    Item getBall();
    Set<Item> getObstacles();
    Set<PlanarRectangle> getLakes();
    Hole getHole();

    record Standard(
            HeightProfile surface,
            Friction surfaceFriction,
            Item ball,
            Set<Item> obstacles,
            Set<PlanarRectangle> lakes,
            Hole hole
    ) implements Course {
        @Override
        public HeightProfile getSurface() {
            return surface;
        }

        @Override
        public Friction getSurfaceFriction() {
            return surfaceFriction;
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
        public Set<PlanarRectangle> getLakes() {
            return lakes;
        }

        @Override
        public Hole getHole() {
            return hole;
        }
    }
}
