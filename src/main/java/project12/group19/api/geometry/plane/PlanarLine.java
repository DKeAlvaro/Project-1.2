package project12.group19.api.geometry.plane;

/**
 * A wrapper for standard line equation {@code y = ax + b} or
 * {@code x = (constant)}.
 *
 * If {@link #isYParallel()} returns false, then {@link #getSlope()}
 * will return {@code a} and {@link #getOffset()} will return {@code b},
 * otherwise, {@link #getSlope()} will return {@code Infinity} and
 * {@link #getOffset()} will return fixed {@code x} value.
 */
public interface PlanarLine {
    /**
     * @return Whether the line is parallel to Y-axis.
     */
    boolean isYParallel();

    /**
     * @return Line slope, unless line is parallel to Y-axis.
     */
    double getSlope();
    double getOffset();

    static PlanarLine through(PlanarCoordinate alpha, PlanarCoordinate beta) {
        if (alpha.equals(beta)) {
            throw new IllegalArgumentException("Can't plot a line through the very same point");
        }

        if (alpha.getX() == beta.getX()) {
            return new YParallel(alpha.getX());
        }

        // TODO: for extreme cases this may provide not-so-correct results due to roundoff
        // maybe it would be better to multiply both numerator and
        // denumerator by b.y + a.y and b.x + a.x? This will at least
        // calculate the difference of squares, which is expected to be
        // greater.
        double slope = (beta.getY() - alpha.getY()) / (beta.getX() - alpha.getX());

        return new Standard(slope, alpha.getY() - (slope * alpha.getX()));
    }

    static PlanarLine through(PlanarCoordinate point, double slope) {
        if (Double.isNaN(slope)) {
            throw new IllegalArgumentException("Passed NaN as slope");
        }

        if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY) {
            return new YParallel(point.getX());
        }

        return new Standard(slope, point.getY() - slope * point.getX());
    }

    record Standard(double slope, double offset) implements PlanarLine {
        @Override
        public boolean isYParallel() {
            return false;
        }

        @Override
        public double getSlope() {
            return slope;
        }

        @Override
        public double getOffset() {
            return offset;
        }
    }

    record YParallel(double x) implements PlanarLine {
        @Override
        public boolean isYParallel() {
            return true;
        }

        @Override
        public double getSlope() {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public double getOffset() {
            return x;
        }
    }
}
