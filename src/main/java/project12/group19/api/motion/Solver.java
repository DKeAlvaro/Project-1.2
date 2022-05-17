package project12.group19.api.motion;

import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.math.ode.Euler;
import project12.group19.math.ode.ODESolver;

import java.util.OptionalDouble;

public class Solver implements MotionCalculator {
    public static final double MOTION_ERROR = 1E-6;
    private static final double h = 0.0000001;
    private static final double g = 9.81;

    private final ODESolver solver;
    private final HeightProfile profile;
    private final Friction friction;

    public Solver(ODESolver solver, HeightProfile profile, Friction friction) {
        this.solver = solver;
        this.profile = profile;
        this.friction = friction;
    }

    private OptionalDouble getXSpeed(MotionState state, double deltaT) {
        return solver.apply(state.getXSpeed(), 0, deltaT, (time, speed) -> {
            return OptionalDouble.of(AccCalculator.accelerationX(
                    profile,
                    state.withXSpeed(speed),
                    friction,
                    deltaT
            ));
        });
    }

    private OptionalDouble getYSpeed(MotionState state, double deltaT) {
        return solver.apply(state.getYSpeed(), 0, deltaT, (time, speed) -> {
            return OptionalDouble.of(AccCalculator.accelerationY(
                    profile,
                    state.withYSpeed(speed),
                    friction,
                    deltaT
            ));
        });
    }

    @Override
    public MotionState calculate(MotionState state, double deltaT) {
        //TODO: deal with .getAsDouble()s
        double xSpeed = getXSpeed(state, deltaT).getAsDouble();
        double ySpeed = getYSpeed(state, deltaT).getAsDouble();

        double x = solver.apply(state.getXPosition(), 0, deltaT, (time, position) -> getXSpeed(state.withXPosition(position), deltaT))
                .getAsDouble();
        double y = solver.apply(state.getYPosition(), 0, deltaT, (time, position) -> getYSpeed(state.withYPosition(position), deltaT))
                .getAsDouble();
        return new MotionState.Standard(xSpeed, ySpeed,x,y);
    }
 

   /* public static void main(String[]args){
        long startTime = System.nanoTime();
        //this was just for testing
        MotionState motionState = new MotionState.Standard(2.0,0.0,0.0,0.0);
        FrictionC friction = new FrictionC(0.1, 0.05);

        double deltaT = 0.0000001;

        HeightProfile heightProfile = (x, y) -> (1/20.0)* (Math.pow(x, 2)+ Math.pow(y,2));//0.1*x +1;//Math.sin((x - y) / 7);
        Solver solver = new Solver(new Euler(), heightProfile, Friction.create(0.1, 0.05));
        while(solver.isMoving(heightProfile, motionState, friction)){
            motionState = solver.calculate(motionState, deltaT);
        }
        long endTime = System.nanoTime();
        long duration = (long) ((endTime - startTime)/Math.pow(10, 6));
        System.out.println(motionState.getXPosition() + " " + motionState.getYPosition());
        System.out.println(".    time: "+ duration + " ms.");
    }*/
}
