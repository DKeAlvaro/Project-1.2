package project12.group19.incubating;
import project12.group19.api.motion.*;

import static project12.group19.incubating.HillClimbing3.*;

public class Shoot {

    private final double xDir;
    private final double yDir;
    private final double startingX;
    private final double startingY;
    private double closestX;
    private double closestY;
    private boolean inHole;

    private boolean inWater = false;
    private double distanceToHole;

    public static Solver solver = HillClimbing3.solver;

    public Shoot(double xDir, double yDir, double startingX, double startingY){
        this.xDir = xDir;
        this.yDir = yDir;
        this.startingX = startingX;
        this.startingY = startingY;
        this.inHole = false;

        getShotDistanceToHole(friction, profile, this);
        combs.add(new Comb(this.getxDir(), this.getYDir(), this.getDistanceToHole()));
        alreadyShot.add(this);
        iterations++;

    }

    public Shoot(double angle, double vel, double startingX,double startingY, boolean b){
        this.xDir = Math.cos(angle) * vel;
        this.yDir = Math.sin(angle) * vel;
        this.startingX = startingX;
        this.startingY = startingY;
        getShotDistanceToHole(friction, profile, this);
        combs.add(new Comb(this.getxDir(), this.getYDir(), this.getDistanceToHole()));
        alreadyShot.add(this);
        iterations++;

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
        return inHole;
    }

    public void setInHole() {
        this.inHole = true;
    }

    public void setDistanceToHole(double distanceToHole) {
        this.distanceToHole = distanceToHole;
    }

    public void setClosestX(double closestX) {
        this.closestX = closestX;
    }

    public void setClosestY(double closestY) {
        this.closestY = closestY;
    }

    public double getClosestX() {
        return closestX;
    }
    public double getClosestY() {
        return closestY;
    }

    public double getStartingX() {
        return startingX;
    }

    public double getStartingY() {
        return startingY;
    }

    public boolean inWater(){
        return inWater;
    }
    public void setInWater(){
        //System.out.println("Shot got into the water! ");
        this.distanceToHole = 10;
        this.inWater = true;
    }

    public boolean hasConverged(){
        return optimiseShot(this).getDistanceToHole() >= distanceToHole || this.inHole;
    }

    public double getAngle(){
        return Math.toDegrees(Math.atan2(this.getYDir(), this.getxDir()));
    }

    public double getVel(){
        return getDistance(xDir, 0, yDir, 0);
    }

}
