import javax.swing.*;
import java.awt.*;

public class GrassComponent extends JComponent {

    private final int DIM = 600;

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.GREEN);



    int x = 0;
    int y = 0;
//
//        GrassTile tile = new GrassTile(x, y);
//
//                tile.draw(g2);

        for (int i = 0; i<24; i++){
            for(int j = 0; j<24; j++){
                GrassTile tile = new GrassTile(x, y);


                g2.setPaint(new Color(54 + (int)(Math.random()*200),54 + (int)(Math.random()*200),54 + (int)(Math.random()*200)));
                tile.draw(g2);
                y=y+25;
            }
            y=0;
            x=x+25;
        }
    }

//    public Color assignColor(int z){
//
//
//    }
    //calc max and min of the function in the plane available

    public static double calcHeight(int x, int y){
        int newX;
        int newY;

        if(x>300){
            newX = x-300;
        }else{
            newX = -(300-x);
        }
        if(y>300){
            newY = -(y-300);
        }else{
            newY = 300-y;
        }
        System.out.println(newX);
        System.out.println(newY);

        double z = (0.5 * ( Math.sin( (newX-newY)/7 ) + 0.9));
    return z;
    }

    public static void main(String[] args) {

        int x = 0;
        int y = 0;

        System.out.println(calcHeight(x,y));
    }


    }
