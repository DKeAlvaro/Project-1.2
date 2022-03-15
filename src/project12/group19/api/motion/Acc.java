package project12.group19.api.motion;

public class Acc implements Acceleration {
    private double x;
    private double y;
    public Acc(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public double getX() {
        
        return x;
    }

    @Override
    public double getY() {
        
        return y;
    }
    
}
