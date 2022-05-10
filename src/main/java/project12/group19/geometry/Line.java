package project12.group19.geometry;

import project12.group19.api.geometry.plane.PlanarCoordinate;

import java.util.Optional;

/**
 * A classic line in 2d plane encoded in form of y = ax + x0.
 *
 * @param slope The "A" coefficient.
 * @param offset The "x0" counterpart.
 */
public record Line(double slope, double offset) {
    public double evaluate(double x) {
        return (x * slope) + offset;
    }

    public Optional<Intersection> intersection(Line other) {
        if (equals(other)) {
            return Optional.of(Intersection.INFINITE);
        }

        // Positions of argument in substractions is correct.
        // Check it yourself by deriving x from a1x + b1 = a2x + b2
        double slopeDifference = slope - other.slope();
        double offsetDifference = other.offset() - offset;

        // Then they are parallel
        if (slopeDifference == 0) {
            return Optional.empty();
        }

        double x = offsetDifference / slopeDifference;
        double y = evaluate(x);

        return Optional.of(Intersection.point(PlanarCoordinate.create(x, y)));
    }

    /**
     * Returns a line that passes through two points.
     *
     * @param a First point.
     * @param b Second point.
     * @return Definition of the line passing through two points.
     */
    public static Line through(PlanarCoordinate a, PlanarCoordinate b) {
        double slope = (b.getY() - a.getY()) / (b.getX() - a.getX());
        double offset = a.getY() - slope * a.getX();
        return new Line(slope, offset);
    }

    public record Intersection(boolean infinite, PlanarCoordinate coordinate) {
        public static Intersection INFINITE = new Intersection(true, null);

        public static Intersection point(PlanarCoordinate coordinate) {
            return new Intersection(false, coordinate);
        }
    }
}
