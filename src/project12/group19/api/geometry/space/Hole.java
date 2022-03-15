package project12.group19.api.geometry.space;

public class Hole {
    private double xHole;
    private double yHole;
    private double radius;

    public Hole(double xHole, double yHole, double radius){
        this.xHole = xHole;
        this.yHole = yHole;
        this.radius = radius;
    }

    public double getxHole(){
        return xHole;
    }
    public double getyHole(){
        return yHole;
    }
    public double getRadius(){
        return radius;
    }
    
}
