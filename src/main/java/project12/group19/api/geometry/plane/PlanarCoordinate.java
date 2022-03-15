package project12.group19.api.geometry.plane;

public interface PlanarCoordinate {
    double getX();
    double getY();

    default double distanceTo(PlanarCoordinate other) {
        return Math.sqrt(Math.pow(getX() - other.getX(), 2) + Math.pow(getY() - other.getY(), 2));
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
