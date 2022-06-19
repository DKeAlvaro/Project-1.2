package project12.group19.api.domain;

import project12.group19.api.geometry.plane.PlanarCoordinate;

import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * Describes a player. On each tick when ball is stable player is asked
 * whether to make a hit, which it can either do (by returning a filled
 * optional) or refuse to (by returning empty optional).
 */
public interface Player {
    Optional<Hit> play(State state) throws FileNotFoundException;
    default Optional<PlanarCoordinate> position(PlanarCoordinate start, PlanarCoordinate end) {
        return Optional.of(start);
    }
}
