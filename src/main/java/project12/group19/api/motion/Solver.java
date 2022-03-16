
package project12.group19.api.motion;

public class Solver implements MotionCalculator{
   
    private double h =  0.00000001; 
    private final double g = 9.81;
     

     
public Solver(){
       

}

    public static void main(String[]args){
        long startTime = System.nanoTime();
        //this was just for testing
        Solver solver = new Solver();
        MotionState motionState = new MotionState.Standard(3,0,-1,-0.5);
        FrictionC friction = new FrictionC(0.2, 0.1);
        
        double deltaT = 0.001;

        while(solver.isMoving(motionState, friction, deltaT)){
            Acc acceleration = solver.acceleration(motionState, friction, deltaT);
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
            double x= state.getXPosition() + state.getXSpeed()* deltaT;
            double y = state.getYPosition() +state.getYSpeed()*deltaT;
            return new MotionState.Standard(xSpeed, ySpeed,x,y);
        }

 
    /**
     * This method checks if the ball is still moving, should be called before computing next state
     * @param motionState Contains velocity x, velocity y, x, y
     * @param friction
     * @param deltaT
     * @return True if the ball is moving, false if the ball stops
     
     */
    public boolean isMoving(MotionState motionState, FrictionC friction, double deltaT){
        double x = motionState.getXPosition();
        double y = motionState.getYPosition();
        if(isVelocity0(motionState, deltaT)){
            if(derivativeHX(x, y)==0 && derivativeHY(x, y)==0 ){
                return false;
            }else if(friction.getStaticCoefficient() > Math.sqrt(Math.pow(derivativeHX(x, y), 2)+ Math.pow(derivativeHY(x, y), 2))){
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
     * @param deltaT
     * @return True if both are 0, false otherwise
     */
    public boolean isVelocity0(MotionState motionState, double deltaT){
        if(motionState.getXSpeed() < deltaT/10 && motionState.getYSpeed() < deltaT/10){
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
    public  Acc acceleration (MotionState motion, FrictionC f, double deltaT){
        double accX;
        double accY;
        if(isVelocity0 (motion, deltaT)){
             accX =(-1)*g* (derivativeHX(motion.getXPosition(), motion.getYPosition()))- f.getDynamicCoefficient() *g*derivativeHX(motion.getXPosition(), motion.getYPosition())/ Math.sqrt(Math.pow(derivativeHX(motion.getXPosition(), motion.getYPosition()), 2)+ Math.pow(derivativeHY(motion.getXPosition(), motion.getYPosition()), 2));
             accY = (-1)*g* (derivativeHY(motion.getXPosition(), motion.getYPosition()))- f.getDynamicCoefficient() *g* derivativeHY(motion.getXPosition(), motion.getYPosition())/ Math.sqrt(Math.pow(derivativeHX(motion.getXPosition(), motion.getYPosition()), 2)+ Math.pow(derivativeHY(motion.getXPosition(), motion.getYPosition()), 2));
        }else{
             accY = (-1)*g*(derivativeHY(motion.getXPosition(), motion.getYPosition())) - (f.getDynamicCoefficient()*g*motion.getYSpeed())/Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2));
             accX = (-1)*g*(derivativeHX(motion.getXPosition(), motion.getYPosition())) - (f.getDynamicCoefficient()*g* motion.getXSpeed())/Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2));
        }
        
        return new Acc(accX, accY);
    }

    
    /**
     * Computes the derivate of h with respect to X
     * @param x
     * @param y
     * @return 
     */
    public double derivativeHX(double x, double y){
        
        return (heightFunction(x+h, y) - heightFunction(x, y))/h;

    }
    /**
     * Computes the derivative of h with respect to Y
     * @param x
     * @param y
     * @return 
     */
    public double derivativeHY(double x, double y){
        return (heightFunction(x, y+h) - heightFunction(x,y))/h;
        
    }
    public double heightFunction (double x, double y){
        // TO DO: get the function from the input file
       //return 0.1 *x +1;
       //this are functions for testing
      return Math.pow(Math.E, -(x*x + y*y)/40);
      //return 0.05 *y +2;
      //return 0.5*(Math.sin((x-y)/7)+0.9);
      //return 0;

    }
    

}
