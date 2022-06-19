package project12.group19.api.geometry.plane;

public interface PlanarCoordinate {
    double getX();
    double getY();

    default double distanceTo(double x, double y) {
        return Math.sqrt(Math.pow(getX() - x, 2) + Math.pow(getY() - y, 2));
    }

    default double distanceTo(PlanarCoordinate other) {
        return distanceTo(other.getX(), other.getY());
    }

    default PlanarCoordinate translate(double x, double y) {
        return create(getX() + x, getY() + y);
    }

    static PlanarCoordinate create(double x, double y) {
        return new Standard(x, y);
    }

    static PlanarCoordinate origin() {
        return Standard.ORIGIN;
    }

    record Standard(double x, double y) implements PlanarCoordinate {
        public static final PlanarCoordinate ORIGIN = new Standard(0, 0);

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }
    }
}
