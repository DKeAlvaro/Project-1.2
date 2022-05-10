package project12.group19.incubating;

public class WaterLake {
    private double startingX;
    private double startingY;
    private double finishingX;
    private double finishingY;

    public WaterLake(double startingX, double finishingX, double startingY, double finishingY){
        this.finishingX = finishingX;
        this.finishingY = finishingY;
        this.startingX = startingX;
        this.startingY = startingY;
    }

    public double getStartingY() {
        return startingY;
    }

    public double getStartingX() {
        return startingX;
    }

    public double getFinishingX() {
        return finishingX;
    }

    public double getFinishingY() {
        return finishingY;
    }
}

