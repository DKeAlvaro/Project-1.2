package project12.group19.api.geometry.plane;

public interface PlanarShape extends PlanarBounded {
    boolean includes(double x, double y);
    default boolean includes(PlanarCoordinate coordinate) {
        return includes(coordinate.getX(), coordinate.getY());
    }

    record Point(PlanarCoordinate coordinate) implements PlanarShape {
        public Point(double x, double y) {
            this(PlanarCoordinate.create(x, y));
        }

        @Override
        public PlanarDimensions getDimensions() {
            return PlanarDimensions.empty();
        }

        @Override
        public PlanarCoordinate getPosition() {
            return coordinate;
        }

        @Override
        public boolean includes(double x, double y) {
            return x == coordinate.getX() && y == coordinate.getY();
        }
    }
}
