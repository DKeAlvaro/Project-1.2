package project12.group19.player.ai.hc;

import project12.group19.incubating.Comb;

import static project12.group19.player.ai.hc.HillClimbingBot.*;

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

    public Shoot(double xDir, double yDir, double startingX, double startingY){
        this.xDir = xDir;
        this.yDir = yDir;
        this.startingX = startingX;
        this.startingY = startingY;
        this.inHole = false;

        getShotDistanceToHole(this);
        combs.add(new Comb(this.getXDir(), this.getYDir(), this.getDistanceToHole()));
        alreadyShot.add(this);
        iterations++;

    }

    public Shoot(double angle, double vel, double startingX,double startingY, boolean b){
        this.xDir = Math.cos(Math.toRadians(angle)) * vel;
        this.yDir = Math.sin(Math.toRadians(angle)) * vel;
        this.startingX = startingX;
        this.startingY = startingY;

    }
    public double getXDir() {
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
        return inHole || optimiseShot(this).getDistanceToHole() >= distanceToHole;
    }

    public double getAngle(){
        return Math.toDegrees(Math.atan2(this.getYDir(), this.getXDir()));
    }

    public double getVel(){
        return getDistance(xDir, 0, yDir, 0);
    }

}
