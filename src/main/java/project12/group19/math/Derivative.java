package project12.group19.math;

import project12.group19.api.domain.Surface;

public class Derivative {
    private static final double h = 0.0000001;
    /**
     * Computes the derivative of h with respect to X
     * @param x
     * @param y
     * @return
     */
    public static double derivativeHX(Surface heightProfile, double x, double y){
        if(heightProfile.getHeight(x+ h,y) == heightProfile.getHeight(x, y) ){
            return 0.0;
        }

        return (heightProfile.getHeight(x+h, y) - heightProfile.getHeight(x, y))/h;

    }
    /**
     * Computes the derivative of h with respect to Y
     * @param x
     * @param y
     * @return
     */
    public static double derivativeHY(Surface heightProfile, double x, double y){
        if(heightProfile.getHeight(x,y+h) == heightProfile.getHeight(x, y) ){
            return 0.0;
        }
        return (heightProfile.getHeight(x, y+h) - heightProfile.getHeight(x,y))/h;

    }
}
