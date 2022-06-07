package project12.group19.api.geometry.plane;

public interface PlanarBounded extends PlanarPositioned, PlanarDimensioned {
    default boolean isInBoundingBox(double x, double y) {
        return x >= getX() && x <= getX() + getWidth() &&
                y >= getY() && y <= getY() + getHeight();
    }
    default boolean isInBoundingBox(PlanarCoordinate coordinate) {
        return isInBoundingBox(coordinate.getX(), coordinate.getY());
    }
}
