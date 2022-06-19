package project12.group19.player.ai.hc;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Combination {
    private double x;
    private double y;
    private double distanceToHole;
    private String comb;

    public Combination(double x, double y, double distanceToHole){
        this.x = round(x, 2);
        this.y = round(y, 2);
        this.distanceToHole = round(distanceToHole, 2);
    }
    public String getComb(){
        comb=this.x+", "+this.y+", "+this.distanceToHole;
        return comb;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    public double round(double a, int places){
        BigDecimal bd = new BigDecimal(Double.toString(a));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double getDistanceToHole() {
        return distanceToHole;
    }
}
