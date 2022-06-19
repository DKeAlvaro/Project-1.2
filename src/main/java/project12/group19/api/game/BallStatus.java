package project12.group19.api.game;

public enum BallStatus {
    /**
     * Ball is being positioned by player
     */
    POSITIONING(false),
    MOVING(false),
    STOPPED(false),
    /**
     * Ball ended up in water.
     */
    DROWNED(true),
    /**
     * Ball fell off the field.
     */
    ESCAPED(true),
    /**
     * The hole was hit
     */
    SCORED(false);

    private final boolean foulTrigger;

    BallStatus(boolean foulTrigger) {
        this.foulTrigger = foulTrigger;
    }

    public boolean isFoulTrigger() {
        return foulTrigger;
    }
}
