package project12.group19.incubating;
import project12.group19.api.motion.*;

import static project12.group19.incubating.HillClimbing2.*;

public class Shoot {

    private final double xDir;
    private final double yDir;
    private final double startingX;
    private final double startingY;
    private double finalX;
    private double finalY;

    private double distanceToHole;

    public static double holeX = HillClimbing2.holeX;
    public static double holeY = HillClimbing2.holeY;

    public static Solver solver = HillClimbing2.solver;

    public Shoot(double xDir, double yDir, double startingX, double startingY){
        this.xDir = xDir;
        this.yDir = yDir;
        this.startingX = startingX;
        this.startingY = startingY;

        MotionState starting = new MotionState.Standard(xDir, yDir, startingX, startingY);
        getShotDistanceToHole(friction, profile, starting, this);

    }
    public double getxDir() {
        return xDir;
    }
    public double getYDir() {
        return yDir;
    }

    public double getDistanceToHole() {
        return distanceToHole;
    }
    public boolean inHole(){
        return distanceToHole < 0.5;
    }

    public static double getHoleX() {
        return holeX;
    }

    public static double getHoleY() {
        return holeY;
    }

    public void setDistanceToHole(double distanceToHole) {
        this.distanceToHole = distanceToHole;
    }

    public void setFinalX(double finalX) {
        this.finalX = finalX;
    }

    public void setFinalY(double finalY) {
        this.finalY = finalY;
    }

    public double getFinalX() {
        return finalX;
    }
    public double getFinalY() {
        return finalY;
    }

    public double getStartingX() {
        return startingX;
    }

    public double getStartingY() {
        return startingY;
    }
}
