package project12.group19.api.geometry.plane;

public interface PlanarDimensions {
    double getWidth();
    double getHeight();

    default PlanarDimensions scale(double widthMultiplier, double heightMultiplier) {
        return new Standard(getWidth() * widthMultiplier, getHeight() * heightMultiplier);
    }

    default PlanarDimensions scale(double multiplier) {
        return scale(multiplier, multiplier);
    }

    default PlanarDimensions scaleWidth(double multiplier) {
        return scale(multiplier, 1);
    }

    default PlanarDimensions scaleHeight(double multiplier) {
        return scale(1, multiplier);
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
