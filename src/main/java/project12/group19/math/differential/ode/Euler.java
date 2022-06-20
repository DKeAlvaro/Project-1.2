package project12.group19.math.differential.ode;

import project12.group19.api.math.differential.ODESolver;
import project12.group19.math.BinaryOperation;

import java.util.OptionalDouble;

public class Euler implements ODESolver {
    @Override
    public OptionalDouble apply(double value, double position, double step, BinaryOperation derivative) {
        OptionalDouble evaluation = derivative.apply(position, value);

        if (evaluation.isEmpty()) {
            return OptionalDouble.empty();
        }

        return OptionalDouble.of(value + evaluation.getAsDouble() * step);
    }
}
