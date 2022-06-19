package project12.group19.api.geometry.plane;

public interface PlanarDimensions {
    double getWidth();
    double getHeight();

    default double getHalfWidth() {
        return getWidth() / 2;
    }

    default double getHalfHeight() {
        return getHeight() / 2;
    }

    default double getSmallerDimension() {
        return Math.min(getWidth(), getHeight());
    }

    default double getLargerDimension() {
        return Math.max(getWidth(), getHeight());
    }

    static PlanarDimensions create(double width, double height) {
        return new Standard(width, height);
    }

    static PlanarDimensions square(double size) {
        return create(size, size);
    }

    record Standard(double width, double height) implements PlanarDimensions {
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
