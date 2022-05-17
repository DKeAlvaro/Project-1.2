package project12.group19.geometry;

import project12.group19.api.geometry.plane.PlanarCoordinate;

/**
 * Describes a line interval. In terms of current application it can be
 * used, for example, as a set of possible ball placements after
 * drowning.
 *
 * @param start Starting point.
 * @param end Ending point
 */
public record LineInterval(PlanarCoordinate start, PlanarCoordinate end) {
    public double getLength() {
        return start.distanceTo(end);
    }
    public double getXLength() {
        return end.getX() - start.getX();
    }

    public double getYLength() {
        return end.getY() - start.getY();
    }

    public Line getLine() {
        return Line.through(start, end);
    }

    /**
     * Allows to get specific point on interval (and beyond), which is
     * placed at {@code offset * interval length} offset relative
     * to starting point. Negative multipliers and multipliers greater
     * than one may be used to get points that lie on the same line, but
     * outside the interval. In other words, this method returns
     * position this interval would be ending at if scaled by
     * {@code offset}.
     *
     * @param offset Number to multiply interval length / relative
     * position of point within the interval.
     * @return A point on the line describing interval which lies at
     * {@code length * offset} relative to starting point.
     */
    public PlanarCoordinate getPoint(double offset) {
        return start.add(getXLength() * offset, getYLength() * offset);
    }

    /**
     * Scales interval by specified multiplier, keeping the start
     * position intact and manipulating the end one.
     *
     * @param multiplier Number to scale. Can be positive, negative and
     * zero.
     *
     * @return Scaled interval.
     */
    public LineInterval scale(double multiplier) {
        return new LineInterval(start, getPoint(multiplier));
    }

    public LineInterval translate(double x, double y) {
        return new LineInterval(start.add(x, y), end.add(x, y));
    }
}
