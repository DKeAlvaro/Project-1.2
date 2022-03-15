import java.awt.*;
import java.awt.geom.Rectangle2D;

public class GrassTile {
    private int xLeft;
    private int yTop;

    public GrassTile(int x, int y) {
        xLeft = x;
        yTop = y;
    }

    public void draw(Graphics2D g2) {
        Rectangle2D tile = new Rectangle(xLeft,yTop,25,25);
        //g2.draw(tile);
        g2.fill(tile);
    }


}
