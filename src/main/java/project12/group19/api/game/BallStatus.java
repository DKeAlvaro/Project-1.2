package project12.group19.api.game;

public enum BallStatus {
    /**
     * Ball is being positioned by player
     */
    POSITIONING,
    MOVING,
    STOPPED,
    /**
     * Ball ended up in water.
     */
    DROWNED,
    /**
     * Ball fell off the field.
     */
    ESCAPED,
    /**
     * The hole was hit
     */
    SCORED
}
