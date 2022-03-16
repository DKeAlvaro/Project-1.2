package project12.group19.api.ui;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GrassComponent extends JComponent {

    private final int DIM = 600;
    private static double[] rangeArr = new double[2];
    private static double[] heights = new double [14400];

    private static int colourIntervals = 10;
    private static double[] intervals = new double[colourIntervals];

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.GREEN);

        int x = 0;
        int y = 0;
        int index = 0;
        for (int i = 0; i<120; i++){
            for(int j = 0; j<120; j++) {
                heights[index] = calcHeight(x,y);
                        y=y+5;
                        index++;
            }
            y=0;
            x=x+5;
            }
        // System.out.println(Arrays.toString(heights));
        getMaxValue(heights);
        getMinValue(heights);
        getIntervals();

    x = 0;
    y = 0;
        for (int i = 0; i<120; i++){
            for(int j = 0; j<120; j++){

                GrassTile tile = new GrassTile(x, y);
                //g2.setPaint(new Color(54 + (int)(Math.random()*200),54 + (int)(Math.random()*200),54 + (int)(Math.random()*200)));
                //g2.setPaint(assignColor(calcHeight(x,y)));
                g2.setPaint(assignColor(calcHeight(x,y)));
                tile.draw(g2);
                y=y+5;
            }
            y=0;
            x=x+5;
        }
    }

    public static Color assignColor(double z){

        if (z<= intervals[0]){
            return new Color (19,98,7);
        }if( z<= intervals[1]){
            return new Color (51,123,36);
        }if (z<= intervals[2]){
            return new Color (79,150,60);
        }if(z<= intervals[3]){
            return new Color (106,177,85);
        }if (z<= intervals[4]){
            return new Color (133,204,111);
        }if (z<= intervals[5]) {
            return new Color(160,233,137);
        }if (z<= intervals[6]) {
            return new Color(189,255,164);
        }if (z<= intervals[7]) {
            return new Color(217,255,191);
        }if (z<= intervals[8]) {
            return new Color(247,255,220);
        }else {
            return new Color(255, 255, 249);
        }


    }
    //calc max and min of the function in the plane available

    public static void getMaxValue(double[] arr){
        double maxValue = arr[0];
        for(int i=1;i < arr.length;i++){
            if(arr[i] > maxValue){
                maxValue = arr[i];
            }
        }
        rangeArr[0] = maxValue;
    }
    public static void getMinValue(double[] arr){
        double minValue = arr[0];
        for(int i=1;i<arr.length;i++){
            if(arr[i] < minValue){
                minValue = arr[i];
            }
        }
        rangeArr[1] = minValue;
    }

    public static void getIntervals(){
        double range = rangeArr[0]-rangeArr[1];
        System.out.println(range);
        double interval = range/10.0000000;

        for(int i = 0; i<intervals.length;i++){
            intervals[i]=interval* (1+i);
        }
        // System.out.println(Arrays.toString(intervals));
    }

    public static double calcHeight(int x, int y){
        double newX;
        double newY;

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

        newX = newX/5.0;
        newY = newY/5.0;

        //double z = (0.5 * ( Math.sin( (newX-newY)/7 ) + 0.9));
        double z = y*x;
        //double z = y*Math.cos(x);
    return z;
    }

    public static void main(String[] args) {
        getIntervals();
        // System.out.println(calcHeight(x,y));
    }

    }
