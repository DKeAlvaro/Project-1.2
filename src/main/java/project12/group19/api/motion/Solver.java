
package project12.group19.api.motion;

public class Solver implements MotionCalculator{
   
    private double h =  0.00000001; 
    private final double g = 9.81;

    public static void main(String[]args){
        //this was just for testing
        Solver solver = new Solver();
        MotionState motionState = new MotionState.Standard(3,0,-1,-0.5);
        FrictionC friction = new FrictionC(0.2, 0.1);

        while(solver.isMoving(motionState, friction)){
            Acc acceleration = solver.acceleration(motionState, friction);
            motionState = solver.calculate(motionState, acceleration, 0.001);
            System.out.print(motionState.getXPosition() + " " + motionState.getYPosition());
            System.out.println(" ");
        }
        
        

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
     * @return True if the ball is moving, false if the ball stops
     */
    public boolean isMoving(MotionState motionState, FrictionC friction){
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
        if(motionState.getXSpeed() < 0.0000001 && motionState.getYSpeed() < 0.0000001){
            return true;
        }else{
            return false;
        }
    }
    

    /**
     * Computes the accelreation in direction X and Y, taking into account if Vx and Vy are both 0 or not
     * @param motion Motion State
     * @param f friction FrictionC
     * @return 
     */
    public  Acc acceleration (MotionState motion, FrictionC f){
        double accX;
        double accY;
        if(isVelocity0 (motion)){
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

    }
    

}
