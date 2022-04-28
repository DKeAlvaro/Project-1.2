package project12.group19.api.motion;

import project12.group19.api.geometry.space.HeightProfile;

import java.util.function.ToDoubleBiFunction;

public class RK2Solver implements MotionCalculator{
    public static final double MOTION_ERROR = 1E-6;
    private static final double h = 0.00000001;
    private static final double g = 9.81;
    private final HeightProfile profile = (x,y) -> (0.1*x +1);;
    private final Friction friction =  new FrictionC(0.1,0.05);


    public static void main(String[]args){
        long startTime = System.nanoTime();
        //this was just for testing
        RK2Solver solver = new RK2Solver();
        MotionState motionState = new MotionState.Standard(2,0,0,0);
        FrictionC friction = new FrictionC(0.2, 0.05);

        double deltaT = 0.0001;

        HeightProfile heightProfile = (x, y) -> (0.1*x +1);//Math.sin((x - y) / 7);
        while(solver.isMoving(heightProfile, motionState, friction, deltaT)){
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
        double x = state.getXPosition();
        double y= state.getYPosition();
        double xSpeed= state.getXSpeed();
        double ySpeed = state.getYSpeed();

        double k1X= calcK(deltaT, xSpeed);
        double k1Y= calcK(deltaT, ySpeed);
        double k1XSpeed= calcK(deltaT, acceleration.getX());
        double k1YSpeed= calcK(deltaT, acceleration.getY());

        MotionState stateTemp= new MotionState.Standard(xSpeed + (2/3.0)* deltaT* k1XSpeed, ySpeed+ (2/3.0)* deltaT* k1YSpeed, x+ (2/3.0)* deltaT* k1X, y+ (2/3.0)* deltaT*k1Y);
        Acc newAcc= acceleration(profile, stateTemp, friction, deltaT); //TO DO friction

        double k2X= calcK(deltaT, xSpeed+ (2/3.0)* deltaT * newAcc.getX());
        double k2Y= calcK(deltaT, ySpeed + (2/3.0) * deltaT * newAcc.getY())  ;
        double k2XSpeed= calcK(deltaT, newAcc.getX());
        double k2YSpeed= calcK(deltaT, newAcc.getY());

        double xProjected = x + (1/4.0) * k1X + (3/4.0) * k2X;
        double yProjected = y + (1/4.0) * k1Y + (3/4.0) * k2Y;
        double xSpeedProjected = xSpeed + (1/4.0) * k1XSpeed + (3/4.0) * k2XSpeed;
        double ySpeedProjected = ySpeed + (1/4.0) * k1YSpeed + (3/4.0) * k2YSpeed;

        return new MotionState.Standard(xSpeedProjected, ySpeedProjected, xProjected, yProjected);
    }

    public double calcK(double step, double der) {
        return step * der;
    }

     /** Computes the derivate of h with respect to X
     * @param x
     * @param y
     * @return
             */
    public static double derivativeHX(HeightProfile heightProfile, double x, double y){

        return (heightProfile.getHeight(x+h, y) - heightProfile.getHeight(x, y))/h;

    }
    /**
     * Computes the derivative of h with respect to Y
     * @param x
     * @param y
     * @return
     */
    public static double derivativeHY(HeightProfile heightProfile, double x, double y){
        return (heightProfile.getHeight(x, y+h) - heightProfile.getHeight(x,y))/h;

    }
    /**
     * Checks if both Vx and Vy are 0, if it is true, diffrent function for coputing acceleration should be used
     * @param motionState
     * @param deltaT
     * @return True if both are 0, false otherwise
     */
    public static boolean isVelocity0(MotionState motionState, double deltaT){
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
    public static Acc acceleration(HeightProfile profile, MotionState motion, Friction f, double deltaT){
        double accX;
        double accY;
        double dhdx = derivativeHX(profile, motion.getXPosition(), motion.getYPosition());
        double dhdy = derivativeHY(profile, motion.getXPosition(), motion.getYPosition());
        double dh = Math.sqrt(dhdx * dhdx + dhdy * dhdy);

        if (!isVelocity0(motion, deltaT)) {
            accY = (-1) * g * (dhdy) - (f.getDynamicCoefficient() * g * motion.getYSpeed()) / Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2));
            accX = (-1) * g * (dhdx) - (f.getDynamicCoefficient() * g * motion.getXSpeed()) / Math.sqrt(Math.pow(motion.getXSpeed(), 2) + Math.pow(motion.getYSpeed(), 2));
        } else if (Math.abs(dh) < MOTION_ERROR) {
            return new Acc(0, 0);
        } else {
            accX = (-1) * g * (dhdx) - f.getDynamicCoefficient() * g * dhdx / dh;
            accY = (-1) * g * (dhdy) - f.getDynamicCoefficient() * g * dhdy / dh;
        }

        return new Acc(accX, accY);
    }

    /**
     * This method checks if the ball is still moving, should be called before computing next state
     * @param motionState Contains velocity x, velocity y, x, y
     * @param friction
     * @param deltaT
     * @return True if the ball is moving, false if the ball stops

     */
    public boolean isMoving(HeightProfile profile, MotionState motionState, FrictionC friction, double deltaT){
        double x = motionState.getXPosition();
        double y = motionState.getYPosition();
        if(isVelocity0(motionState, deltaT)){
            double dhdx = derivativeHX(profile, x, y);
            double dhdy = derivativeHY(profile, x, y);
            if(dhdx ==0 && dhdy ==0 ){
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

}
