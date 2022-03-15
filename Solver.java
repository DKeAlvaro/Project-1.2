import project12.group19.api.motion.Acceleration;
import project12.group19.api.motion.Friction;
import project12.group19.api.motion.MotionCalculator;
import project12.group19.api.motion.MotionState;

public class Solver implements MotionCalculator{
   
    private double h =  0.00000001; 
    private final double g = 9.81;
     

     
public Solver(){
       

}
    
    /*kurt:  some thoughts, feel free to change
    
    1. a boolean method to decide if the ball stop in the target area
    
    public boolean hitTarget(double x, double y, double xt, double yt, double r){
        return (Math.pow(x-xt, 2)+Math.pow(y-yt, 2) <= Math.pow(r, 2))          //  is that ok when the ball lay on the boarder? 
    }
    
    
    
    2. initial acceleration.
     its simply -1*uk*g. Check the equation, since the deltaH&deltaX is 0, vy also 0(assume we start on the even ground.)
     incase not even ground, we can use
     (uk*g*xv)/Math.sqrt(xv*xv+yv*yv)
      
    3. a boolean method to check if we hit the border of game.  the ball should stop if we hit the border.
    currently we lack some parameters, such as the size of gui, the loaction of 
    
    
    */

    


    public static void main(String[]args){
        solverAttempt1 solver = new solverAttempt1(0.06, 0.1, 0, 0, 1, 0);
        
        System.out.println(solver.derivativeHX(1000, 1));

    }
    @Override
        public MotionState calculate(MotionState state, Acceleration acceleration, double deltaT) {
            
            if(state.getXSpeed() == 0 && state.getYSpeed()==0){

            }
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
     * @return True if the ball is moving, false if the ball stops
     */
    public boolean isMoving(MotionState motionState, Friction friction){
        double x = motionState.getXPosition();
        double y = motionState.getYPosition();
        if(isVelocity0(motionState)){
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
     * @return True if both are 0, false otherwise
     */
    public boolean isVelocity0(MotionState motionState){
        if(motionState.getXSpeed() == 0 && motionState.getYSpeed() == 0){
            return true;
        }else{
            return false;
        }
    }
    

    /**
     * Computes the accelreation in direction X and Y, taking into account if Vx and Vy are both 0 or not
     * @param motion
     * @param f friction
     * @return 
     */
    public  Acceleration accelerationY (MotionState motion, Friction f){
        if(isVelocity0 (motion)){
            double accX =(-1)*g* (derivativeHX(motion.getXPosition(), motion.getYPosition()))- f.getDynamicCoefficient() *g*derivativeHX(motion.getXPosition(), motion.getYPosition())/ Math.sqrt(Math.pow(derivativeHX(motion.getXPosition(), motion.getYPosition()), 2)+ Math.pow(derivativeHY(motion.getXPosition(), motion.getYPosition()), 2));
            double accY = (-1)*g* (derivativeHY(motion.getXPosition(), motion.getYPosition()))- f.getDynamicCoefficient() *g* derivativeHY(motion.getXPosition(), motion.getYPosition())/ Math.sqrt(Math.pow(derivativeHX(motion.getXPosition(), motion.getYPosition()), 2)+ Math.pow(derivativeHY(motion.getXPosition(), motion.getYPosition()), 2));
        }else{
            double accY = (-1)*g*(derivativeHY(motion.getXPosition(), motion.getYPosition())) - (f.getDynamicCoefficient()*g*motion.getYSpeed)/Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2));
            double accX = (-1)*g*(derivativeHX(motion.getXPosition(), motion.getYPosition())) - (f.getDynamicCoefficient()*g* motion.getXSpeed())/Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2));
        }
        
        return new Acceleration (accX, accY);
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
        return Math.pow(x, 3);

    }
    

}
