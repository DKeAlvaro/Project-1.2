package project12.group19.api.geometry.plane;

/**
 * A shape fixed at some point in the space of course. <b>The position
 * always corresponds to top left corner and not the center</b>.
 * Exposed dimensions allow to compute bounding box for the shape.
 */
public interface PlanarShape extends PlanarPositioned, PlanarDimensioned {
    /**
     * @param x X-axis position component.
     * @param y Y-axis position component.
     * @return Whether specified point falls within shape.
     */
    boolean includes(double x, double y);
    default boolean includes(PlanarCoordinate coordinate) {
        return includes(coordinate.getX(), coordinate.getY());
    }

    /**
     * @return The center point of the shape. Please note that it is not
     * guaranteed to lie within the shape, though this project is
     * unlikely to contain such examples.
     */
    default PlanarCoordinate getCenter() {
        return getPosition().translate(getHalfWidth(), getHalfHeight());
    }

    /**
     * TODO: replace {@link PlanarRectangle} with this
     */
    record Rectangle(PlanarCoordinate position, PlanarDimensions dimensions) implements PlanarShape {
        @Override
        public PlanarDimensions getDimensions() {
            return dimensions;
        }

        @Override
        public PlanarCoordinate getPosition() {
            return position;
        }

        @Override
        public boolean includes(double x, double y) {
            return within(x, getX(), getWidth()) && within(y, getY(), getHeight());
        }

        private static boolean within(double value, double start, double range) {
            return value >= start && value <= start + range;
        }
    }

    record Ellipse(PlanarCoordinate position, PlanarDimensions dimensions) implements PlanarShape {
        @Override
        public PlanarDimensions getDimensions() {
            return dimensions;
        }

        @Override
        public PlanarCoordinate getPosition() {
            return position;
        }

        @Override
        public boolean includes(double x, double y) {
            PlanarCoordinate center = getCenter();
            double xOffset = x - center.getX();
            double yOffset = y - center.getY();
            return Math.pow(xOffset / getHalfWidth(), 2) + Math.pow(yOffset / getHalfHeight(), 2) <= 1;
        }

        public static PlanarShape centered(PlanarCoordinate center, PlanarDimensions dimensions) {
            return new Ellipse(center.translate(-dimensions.getWidth() / 2, -dimensions.getHeight() / 2), dimensions);
        }

        public static PlanarShape centered(PlanarCoordinate center, double width, double height) {
            return centered(center, PlanarDimensions.create(width, height));
        }

        public static PlanarShape centered(double x, double y, double width, double height) {
            return centered(PlanarCoordinate.create(x, y), width, height);
        }
    }
}
