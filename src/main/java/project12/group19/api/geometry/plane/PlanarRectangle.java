package project12.group19.api.geometry.plane;

/**
 * TODO: either convert to inner record of {@link PlanarShape} or extract
 * {@link project12.group19.api.geometry.plane.PlanarShape.Point}
 * from there
 */
public interface PlanarRectangle extends PlanarShape {
    default boolean includes(double x, double y) {
        return isInBoundingBox(x, y);
    }

    default PlanarCoordinate getCenter() {
        return PlanarCoordinate.create(getX() + (getWidth() / 2), getY() + (getHeight() / 2));
    }

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
