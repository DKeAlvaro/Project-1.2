package project12.group19.ui.swing;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Class to create a single grass tile using Graphics 2D.
 */
public class GrassTile {
    private int xLeft;
    private int yTop;

    /**
     * Constructor of the object instance.
     * @param x x coordinate of the tile
     * @param y y coordinate of the tile
     */
    public GrassTile(int x, int y) {
        xLeft = x;
        yTop = y;
    }

    /**
     * Method stroking and filling the shape.
     * @param g2 an instance of the Graphics2D class
     */
    public void draw(Graphics2D g2) {
        Rectangle2D tile = new Rectangle(xLeft,yTop,5,5);
        g2.fill(tile);
    }


}
