package project12.group19.api.game.lifecycle;

import project12.group19.api.domain.Player;
import project12.group19.api.geometry.plane.PlanarCoordinate;

/**
 * @param index
 * @param start
 * @param hit
 * @param status
 * @param position
 */
public record Round(int index, PlanarCoordinate start, Player.Hit hit, BallStatus status, PlanarCoordinate position) {}
