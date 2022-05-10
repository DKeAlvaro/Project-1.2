package project12.group19.api.geometry.plane;

public interface PlanarShape {
    boolean contains(double x, double y);
    default boolean contains(PlanarCoordinate coordinate) {
        return contains(coordinate.getX(), coordinate.getY());
    }
}
