package project12.group19.api.geometry.space;

import project12.group19.api.geometry.plane.PlanarCoordinate;

/**
 * A wrapper for a function describing course surface.
 *
 * @deprecated Use {@link project12.group19.api.domain.Surface}
 *
 * TODO: remove completely.
 */
@Deprecated
public interface HeightProfile {
    double getHeight(double x, double y);

    default double getHeight(PlanarCoordinate coordinate) {
        return getHeight(coordinate.getX(), coordinate.getY());
    }
}
