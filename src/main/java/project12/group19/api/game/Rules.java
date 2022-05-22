package project12.group19.api.game;

import project12.group19.api.geometry.plane.PlanarRectangle;

import java.util.OptionalInt;

public interface Rules {
    OptionalInt getAllowedFouls();
    OptionalInt getRoundLimit();

    PlanarRectangle getFieldBoundaries();

    /**
     * @return Whether to force ball placement if player hasn't given a
     * decision on where to place it (after drowning, for example).
     */
    boolean forceBallPlacement();

    record Standard(
            OptionalInt allowedFouls,
            OptionalInt roundLimit,
            PlanarRectangle fieldBoundaries,
            boolean forceBallPlacement
    ) implements Rules {
        @Override
        public OptionalInt getAllowedFouls() {
            return allowedFouls;
        }

        @Override
        public OptionalInt getRoundLimit() {
            return roundLimit;
        }

        @Override
        public PlanarRectangle getFieldBoundaries() {
            return fieldBoundaries;
        }
    }
}
