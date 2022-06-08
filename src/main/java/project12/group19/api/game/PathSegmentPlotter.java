package project12.group19.api.game;

import project12.group19.api.domain.Player;
import project12.group19.api.game.path.WaySpot;

import java.util.List;
import java.util.Optional;

/**
 * Plots connection between two wayspots in format of list of required
 * hits to move from starting spot to target.
 */
public interface PathSegmentPlotter {
    Optional<List<Player.Hit>> plot(WaySpot source, WaySpot target);
}
