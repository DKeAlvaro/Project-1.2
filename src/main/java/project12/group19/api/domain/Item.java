package project12.group19.api.domain;

import project12.group19.api.geometry.plane.PlanarCoordinate;

public interface Item {
    String getType();
    PlanarCoordinate getCoordinate();

    record Standard(
            String type,
            PlanarCoordinate coordinate
    ) implements Item {
        @Override
        public String getType() {
            return type;
        }

        @Override
        public PlanarCoordinate getCoordinate() {
            return coordinate;
        }

        public Item withCoordinate(PlanarCoordinate coordinate) {
            return new Standard(type, coordinate);
        }
    }
}
