package project12.group19.physics.motion;

import project12.group19.api.domain.Surface;
import project12.group19.api.math.differential.ODESolver;
import project12.group19.api.physics.motion.AccelerationCalculator;
import project12.group19.api.physics.motion.MotionCalculator;
import project12.group19.api.physics.motion.MotionState;

import java.util.OptionalDouble;

public class StandardMotionCalculator implements MotionCalculator {
    private final ODESolver solver;
    private final Surface surface;
    private final AccelerationCalculator calculator;

    public StandardMotionCalculator(ODESolver solver, Surface surface, AccelerationCalculator calculator) {
        this.solver = solver;
        this.surface = surface;
        this.calculator = calculator;
    }

    private OptionalDouble getXSpeed(MotionState state, double deltaT) {
        return solver.apply(state.getXSpeed(), 0, deltaT, (time, speed) -> {
            return OptionalDouble.of(calculator.getXAcceleration(
                    surface,
                    state.withXSpeed(speed),
                    deltaT
            ));
        });
    }

    private OptionalDouble getYSpeed(MotionState state, double deltaT) {
        return solver.apply(state.getYSpeed(), 0, deltaT, (time, speed) -> {
            return OptionalDouble.of(calculator.getYAcceleration(
                    surface,
                    state.withYSpeed(speed),
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
