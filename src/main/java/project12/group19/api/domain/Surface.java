package project12.group19.api.domain;

import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.motion.Friction;

/**
 * This interface ties up available information about course surface for
 * downstream consumers to use.
 */
public interface Surface {
    /**
     * @param x Position, x-axis component.
     * @param y Position, y-axis component.
     * @return Height of the course at specific point.
     */
    double getHeight(double x, double y);

    /**
     * @param x Position, x-axis component.
     * @param y Position, y-axis component.
     * @return Friction associated with a specific point.
     */
    Friction getFriction(double x, double y);

    default double getHeight(PlanarCoordinate coordinate) {
        return getHeight(coordinate.getX(), coordinate.getY());
    }

    default Friction getFriction(PlanarCoordinate coordinate) {
        return getFriction(coordinate.getX(), coordinate.getY());
    }
}
