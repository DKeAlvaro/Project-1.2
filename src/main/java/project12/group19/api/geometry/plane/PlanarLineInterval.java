package project12.group19.api.geometry.plane;

public interface PlanarLineInterval {
    PlanarLine getLine();
    PlanarCoordinate getStart();
    PlanarCoordinate getEnd();

    record Standard(PlanarLine line, PlanarCoordinate start, PlanarCoordinate end) implements PlanarLineInterval {
        @Override
        public PlanarLine getLine() {
            return line;
        }

        @Override
        public PlanarCoordinate getStart() {
            return start;
        }

        @Override
        public PlanarCoordinate getEnd() {
            return end;
        }
    }
}
