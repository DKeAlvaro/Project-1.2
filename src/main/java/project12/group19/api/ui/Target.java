package main.java.project12.group19.api.ui;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Target {
    // Initializing the fields
    private double xLeft;
    private double yTop;
    private double radius;

    /**
     * Constructor of the target
     * @param x position on X-axis of the coordinates from solver
     * @param y position on Y-axis of the coordinates from solver
     * @param r radius of the target
     */
    public Target(int x, int y, double r){
        radius = r*12;
        xLeft = GrassComponent.coorToSwingX(x*12) - r*6;
        yTop = GrassComponent.coorToSwingY(y*12) - r*6;
    }

    /**
     * Drawing the target
     * @param g2 an instance of the Graphics2D class.
     */
    public void draw(Graphics2D g2) {
        Ellipse2D.Double target = new Ellipse2D.Double(xLeft,yTop,2*radius,2*radius);
        g2.fill(target);
    }



}
