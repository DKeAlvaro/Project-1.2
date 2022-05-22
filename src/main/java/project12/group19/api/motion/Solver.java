package project12.group19.api.motion;

import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.math.ode.ODESolver;

import java.util.OptionalDouble;

public class Solver implements MotionCalculator {
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
}
