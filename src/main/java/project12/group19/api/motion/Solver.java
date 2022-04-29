
package project12.group19.api.motion;

import project12.group19.api.geometry.space.HeightProfile;

public class Solver implements MotionCalculator {
    public static final double MOTION_ERROR = 1E-6;
   
    private static final double h = 0.0000001;
    private static final double g = 9.81;

    public static void main(String[]args){
        long startTime = System.nanoTime();
        //this was just for testing
        Solver solver = new Solver();
        MotionState motionState = new MotionState.Standard(2.0,0.0,0.0,0.0);
        FrictionC friction = new FrictionC(0.1, 0.05);
        
        double deltaT = 0.0000001;

        HeightProfile heightProfile = (x, y) -> (1/20.0)* (Math.pow(x, 2)+ Math.pow(y,2));//0.1*x +1;//Math.sin((x - y) / 7);
        while(solver.isMoving(heightProfile, motionState, friction)){
            Acc acceleration = solver.acceleration(heightProfile, motionState, friction, deltaT);
            motionState = solver.calculate(motionState, acceleration, deltaT);

            
            
        }
        long endTime = System.nanoTime();
        long duration = (long) ((endTime - startTime)/Math.pow(10, 6));
        System.out.println(motionState.getXPosition() + " " + motionState.getYPosition());
        System.out.println(".    time: "+ duration + " ms.");
        


        
        

    }
    @Override
        public MotionState calculate(MotionState state, Acceleration acceleration, double deltaT) {
            double xSpeed = state.getXSpeed() + acceleration.getX() *deltaT;
            double ySpeed = state.getYSpeed() + acceleration.getY() *deltaT;
           // if(ySpeed< MOTION_ERROR){
            //    ySpeed=0.0;
            //}
            double x= state.getXPosition() + state.getXSpeed()* deltaT;
            double y = state.getYPosition() +state.getYSpeed()*deltaT;
            return new MotionState.Standard(xSpeed, ySpeed,x,y);
        }

 
    /**
     * This method checks if the ball is still moving, should be called before computing next state
     * @param motionState Contains velocity x, velocity y, x, y
     * @param friction

     * @return True if the ball is moving, false if the ball stops
     
     */
    public boolean isMoving(HeightProfile profile, MotionState motionState, FrictionC friction){
        double x = motionState.getXPosition();
        double y = motionState.getYPosition();
        if(isVelocity0(motionState)){
            double dhdx = derivativeHX(profile, x, y);
            double dhdy = derivativeHY(profile, x, y);
            if(Math.abs(dhdx) < MOTION_ERROR && Math.abs(dhdy) <MOTION_ERROR ){
                return false;
            }else if(friction.getStaticCoefficient() > Math.sqrt(Math.pow(dhdx, 2)+ Math.pow(dhdy, 2))){
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }

    }
    /**
     * Checks if both Vx and Vy are 0, if it is true, diffrent function for coputing acceleration should be used
     * @param motionState

     * @return True if both are 0, false otherwise
     */
    public static boolean isVelocity0(MotionState motionState){
        if(Math.abs(motionState.getXSpeed()) < MOTION_ERROR && Math.abs(motionState.getYSpeed()) < MOTION_ERROR){
            return true;
        }else{
            return false;
        }
    }
    

    /**
     * Computes the accelreation in direction X and Y, taking into account if Vx and Vy are both 0 or not
     * @param motion Motion State
     * @param f friction FrictionC
     * @param deltaT
     * @return 
     */
    public static Acc acceleration(HeightProfile profile, MotionState motion, Friction f, double deltaT){
        double accX;
        double accY;
        double dhdx = derivativeHX(profile, motion.getXPosition(), motion.getYPosition());
        double dhdy = derivativeHY(profile, motion.getXPosition(), motion.getYPosition());
        double dh = Math.sqrt(Math.pow(dhdx, 2) + Math.pow(dhdy, 2));


       if (!isVelocity0(motion)) {
            accY = (-1) * g * (dhdy) - (f.getDynamicCoefficient() * g * motion.getYSpeed()) / Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2));
            accX = (-1) * g * (dhdx) - (f.getDynamicCoefficient() * g * motion.getXSpeed()) / Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2));
        } else if (Math.abs(dh) > MOTION_ERROR) {
           accX = (-1) * g * (dhdx) - (f.getDynamicCoefficient() * g * dhdx / dh);
      
           accY = (-1) * g * (dhdy) - (f.getDynamicCoefficient() * g * dhdy / dh);

        } else {
           return new Acc(0, 0);

        }

        return new Acc(accX, accY);
    }

    
    /**
     * Computes the derivative of h with respect to X
     * @param x
     * @param y
     * @return 
     */
    public static double derivativeHX(HeightProfile heightProfile, double x, double y){
       /* if(heightProfile.getHeight(x+ h,y) == heightProfile.getHeight(h, y) ){
            return 0.0;
        }*/
        
        return (heightProfile.getHeight(x+h, y) - heightProfile.getHeight(x, y))/h;

    }
    /**
     * Computes the derivative of h with respect to Y
     * @param x
     * @param y
     * @return 
     */
    public static double derivativeHY(HeightProfile heightProfile, double x, double y){
       if(heightProfile.getHeight(x,y+h) == heightProfile.getHeight(x, h) ){
            return 0.0;
        }
        return (heightProfile.getHeight(x, y+h) - heightProfile.getHeight(x,y))/h;
        
    }
    public double heightFunction (double x, double y){
        // TO DO: get the function from the input file
       //return 0.1 *x +1;
       //this are functions for testing
      return (1/20.0)*(Math.pow(x,2)+ Math.pow(y,2));
      //return 0.05 *y +2;
      //return 0.5*(Math.sin((x-y)/7)+0.9);
      //return 0;

    }
    

}
