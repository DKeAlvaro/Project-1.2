package main.java.project12.group19.api.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Class to create background of the game consisting of grass tiles and target point.
 */
public class GrassComponent extends JComponent {

    private static double[] rangeArr = new double[2];
    private static double[] heights = new double [14400];
    private static double[] intervals = new double[12];


    /**
     * Method to draw grass tiles and target.
     * @param g an instance of the Graphics class
     */
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        getHeights();
        findMax(heights);
        findMin(heights);
        getIntervals();

        int x = 0;
        int y = 0;
        int id = 0;
        for (int i = 0; i<120; i++){
            for(int j = 0; j<120; j++){

                GrassTile tile = new GrassTile(x, y);
                g2.setPaint(assignColor(heights[id]));
                tile.draw(g2);
                y=y+5;
                id++;
            }
            y=0;
            x=x+5;
        }

        Target target = new Target(-10,-5,3);
        g2.setPaint(Color.BLACK);
        target.draw(g2);
    }

    /**
     * Method to calculate height of each tile in the field.
     */
    public static void getHeights(){
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
    }

    /**
     * Method assigning color to a tile depending on its height
     * @param z height of the tile being colored
     * @return Color to be applied
     */
    public static Color assignColor(double z) {
        if (z <= intervals[0]) {
            return new Color (0,100,0);
        }
        if (z <= intervals[1]) {
            return new Color (0,110,0);
        }
        if (z <= intervals[2]) {
            return new Color (0,120,0);
        }
        if (z <= intervals[3]) {
            return new Color (0,130,0);
        }
        if (z <= intervals[4]) {
            return new Color (0,140,0);
        }
        if (z <= intervals[5]) {
            return new Color (0,150,0);
        }
        if (z <= intervals[6]) {
            return new Color (0,160,0);
        }
        if (z <= intervals[7]) {
            return new Color (0,170,0);
        }
        if (z <= intervals[8]) {
            return new Color (0,180,0);
        }
        if (z <= intervals[9]) {
            return new Color (0,190,0);
        }
        if (z <= intervals[10]) {
            return new Color (0,200,0);
        }
        if (z <= intervals[11]) {
            return new Color (0,210,0);
        }
        else {
            return new Color (0,220,0);
        }
    }

    /**
     * Method to find the highest value of the heights of tiles.
     * @param arr Array of tile heights to be searched.
     */
    public static void findMax(double[] arr){
        double max = arr[0];
        for(int i=1;i < arr.length;i++){
            if(arr[i] > max){
                max = arr[i];
            }
        }
        rangeArr[0] = max;
    }

    /**
     * Method to find the lowest value of the heights of tiles.
     * @param arr Array of tile heights to be searched.
     */
    public static void findMin(double[] arr){
        double min = arr[0];
        for(int i=1;i<arr.length;i++){
            if(arr[i] < min){
                min = arr[i];
            }
        }
        rangeArr[1] = min;
    }

    /**
     * Method to find and range of function in the field and intervals for coloring the tiles.
     */
    public static void getIntervals(){
        double range = rangeArr[0]-rangeArr[1];
        double interval2 = range/20.000;

        for (int i = 0; i< intervals.length; i++){
            intervals[i] = interval2 * (1+i);
        }
    }

    /**
     * Method to calculate height profile of a given x,y point.
     * @param x x - coordinate of the point
     * @param y y - coordinate of the point
     * @return height of the point
     */
    public static double calcHeight(int x, int y){
        double newX =coorToRealX(x);
        double newY = coorToRealY(y);


        newX = newX/10.0;
        newY = newY/10.0;

        double z = 0.1*x+1;
        //connection to engine to have function
        return z;
    }

    /**
     * Method to convert real x-coordinate to Swing coordinates.
     * @param x point to be converted (relative to real world)
     * @return x value relative to Swing coordinates
     */
    public static int coorToSwingX(int x){

        int newX;
        if (x <= 0) {
            newX = 300 - Math.abs(x);
        } else {
            newX = 300 + x;
        }
        return newX;
    }
    /**
     * Method to convert real y-coordinate to Swing coordinates.
     * @param y coordinate to be converted (relative to real world)
     * @return x value relative to Swing coordinates
     */
    public static int coorToSwingY(int y){
        int newY;
        if (y >= 0) {
            newY = 300 - y;
        } else {
            newY = 300 + Math.abs(y);
        }
        return newY;
    }

    /**
     * Method to convert Swing x-coordinate to real world.
     * @param x coordinate to be converted (relative to Swing plane)
     * @return x value relative to Swing coordinates
     */
    public static int coorToRealX (int x){
        int newX;
        if(x>300){
            newX = x-300;
        }else{
            newX = -(300-x);
        }
        return newX;
    }
    /**
     * Method to convert Swing x-coordinate to real world.
     * @param y coordinate to be converted (relative to Swing plane)
     * @return y value relative to Swing coordinates
     */
    public static int coorToRealY (int y){
        int newY;
        if(y>300){
            newY = -(y-300);
        }else{
            newY = 300-y;
        }
        return newY;
    }

}
