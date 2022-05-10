package project12.group19.api.domain;

import project12.group19.api.geometry.plane.PlanarCoordinate;

/**
 * An additional item on map, such as tree or lake.
 */
public interface Item {
    String getType();

    /**
     * @return Coordinate of item. Please note that it is not the center
     * of item, it's its topmost leftmost coordinate on xy-plane (the
     * smallest possible x and y).
     */
    PlanarCoordinate getCoordinate();

    /**
     * @return Arbitrary item size. Please note that it is not in any
     * specific units, it's a multiplier of a "base" size (scale).
     */
    double getSize();

    record Standard(
            String type,
            PlanarCoordinate coordinate,
            double size
    ) implements Item {
        @Override
        public String getType() {
            return type;
        }

        @Override
        public PlanarCoordinate getCoordinate() {
            return coordinate;
        }

        @Override
        public double getSize() {
            return size;
        }

        public Item withCoordinate(PlanarCoordinate coordinate) {
            return new Standard(type, coordinate, size);
        }
    }
}
