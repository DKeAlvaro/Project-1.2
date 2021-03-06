package project12.group19.api.geometry.plane;

/**
 * @deprecated use {@link PlanarShape.Rectangle}
 *
 * TODO: remove completely
 */
@Deprecated
public interface PlanarRectangle extends PlanarPositioned, PlanarDimensioned {
    static PlanarRectangle create(PlanarCoordinate coordinate, PlanarDimensions dimensions) {
        return new Standard(coordinate, dimensions);
    }

    static PlanarRectangle create(double x, double y, double width, double height) {
        return create(PlanarCoordinate.create(x, y), PlanarDimensions.create(width, height));
    }

    static PlanarRectangle create(double x, double y, PlanarDimensions dimensions) {
        return create(PlanarCoordinate.create(x, y), dimensions);
    }

    static PlanarRectangle create(PlanarCoordinate coordinate, double width, double height) {
        return create(coordinate, PlanarDimensions.create(width, height));
    }

    default boolean includes(PlanarCoordinate position) {
        boolean inY = position.getY() >= getY() && position.getY() <= getY() + getHeight();
        boolean inX = position.getX() >= getX() && position.getX() <= getX() + getWidth();
        return inY && inX;
    }

    record Standard(
            PlanarCoordinate position,
            PlanarDimensions dimensions
    ) implements PlanarRectangle {
        public Standard(double x, double y, double width, double height) {
            this(PlanarCoordinate.create(x, y), PlanarDimensions.create(width, height));
        }

        @Override
        public PlanarCoordinate getPosition() {
            return position;
        }

        @Override
        public PlanarDimensions getDimensions() {
            return dimensions;
        }
    }
}
