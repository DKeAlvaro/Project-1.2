package project12.group19.api.geometry.plane;

public interface PlanarRectangle extends PlanarShape, PlanarPositioned, PlanarDimensioned {
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

    static PlanarRectangle square(PlanarCoordinate coordinate, double size) {
        return square(coordinate.getX(), coordinate.getY(), size);
    }

    static PlanarRectangle square(double x, double y, double size) {
        return create(x, y, size, size);
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

        @Override
        public boolean contains(double x, double y) {
            return x >= position.getX() && x <= position.getX() + dimensions.getWidth() &&
                    y >= position.getY() && y <= position.getY() + dimensions.getHeight();
        }
    }
}
