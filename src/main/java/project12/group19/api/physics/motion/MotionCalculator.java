package project12.group19.api.physics.motion;

import project12.group19.api.geometry.plane.PlanarCoordinate;

public interface MotionCalculator {
    /**
     * Takes current motion state and calculates the next at moment of
     * time {@code currentTime + deltaT}.
     *
     * @param state Current item state.
     * @param deltaT Number of seconds (fraction of second) "in future"
     * relative to current state at which next state should be computed.
     * @return New motion state for this specific item.
     */
    MotionState calculate(MotionState state, double deltaT);

    /**
     * A dummy implementation that would make the ball rotate around origin
     */
    class Circular implements MotionCalculator {
        private final PlanarCoordinate origin;

        public Circular(PlanarCoordinate origin) {
            this.origin = origin;
        }

        @Override
        public MotionState calculate(MotionState state, double deltaT) {
            double radius = origin.distanceTo(PlanarCoordinate.create(state.getXPosition(), state.getYPosition()));
            double x = state.getXPosition() - origin.getX();
            double y = state.getYPosition() - origin.getY();
            double tangent = Math.atan2(y, x);
            double angle = tangent + (Math.PI / 180); // 1 degree increment per update
            return new MotionState.Standard(
                    state.getXSpeed(),
                    state.getYSpeed(),
                    origin.getX() + radius * Math.cos(angle),
                    origin.getY() + radius * Math.sin(angle)
            );
        }

        public static MotionCalculator rooted() {
            return new Circular(PlanarCoordinate.origin());
        }
    }
}
