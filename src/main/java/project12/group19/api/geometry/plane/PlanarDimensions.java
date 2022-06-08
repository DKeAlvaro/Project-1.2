package project12.group19.api.geometry.plane;

public interface PlanarDimensions {
    double getWidth();
    double getHeight();

    static PlanarDimensions create(double width, double height) {
        return new Standard(width, height);
    }

    static PlanarDimensions empty() {
        return Standard.EMPTY;
    }

    record Standard(double width, double height) implements PlanarDimensions {
        private static final PlanarDimensions EMPTY = new Standard(0, 0);

        @Override
        public double getWidth() {
            return width;
        }

        @Override
        public double getHeight() {
            return height;
        }
    }
}
