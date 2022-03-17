package project12.group19.api.ui;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Target {
    private double xLeft;
    private double yTop;
    private double radius;

    public Target(int x, int y, double r){
        radius = r*12;
        xLeft = GrassComponent.coorToSwingX(x*12) - r*12;
        yTop = GrassComponent.coorToSwingY(y*12) - r*12;

    }
    public void draw(Graphics2D g2) {
        Ellipse2D.Double target = new Ellipse2D.Double(xLeft,yTop,2*radius,2*radius);
        g2.fill(target);
    }

}
