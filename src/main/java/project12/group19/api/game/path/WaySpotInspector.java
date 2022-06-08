package project12.group19.api.game.path;

import java.util.Set;

/**
 * This interface provides access for path-finding algorithms to sibling
 * wayspots of a single wayspot. The on-demand manner allows both
 * ahead-of-time and just-in-time computations, which in turn allows not
 * only discovering all wayspots at once, but also gradually exploring
 * the map only for specific spots, reducing the computation time.
 */
public interface WaySpotInspector {
    Set<WaySpot> getNeighbors(WaySpot spot);
}
