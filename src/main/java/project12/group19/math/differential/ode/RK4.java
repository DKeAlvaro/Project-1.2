package project12.group19.math.differential.ode;

import project12.group19.api.math.differential.ODESolver;
import project12.group19.math.BinaryOperation;

import java.util.OptionalDouble;

public class RK4 implements ODESolver {
    @Override
    public OptionalDouble apply(double value, double position, double step, BinaryOperation derivative) {
        OptionalDouble d1 = derivative.apply(position, value);
        OptionalDouble k1 = calcK(step, d1);

        if (k1.isEmpty()) {
            return OptionalDouble.empty();
        }

        OptionalDouble d2 = derivative.apply(position + step / 2, value + k1.getAsDouble() / 2);
        OptionalDouble k2 = calcK(step, d2);

        if (k2.isEmpty()) {
            return OptionalDouble.empty();
        }

        OptionalDouble d3 = derivative.apply(position + step / 2, value + k2.getAsDouble() / 2);
        OptionalDouble k3 = calcK(step, d3);

        if (k3.isEmpty()) {
            return OptionalDouble.empty();
        }

        OptionalDouble d4 = derivative.apply(position + step, value + k3.getAsDouble());
        OptionalDouble k4 = calcK(step, d4);

        if (k4.isEmpty()) {
            return OptionalDouble.empty();
        }

        return OptionalDouble.of(value + (1/6.0) * (k1.getAsDouble() + 2.0 * k2.getAsDouble() + 2.0 * k3.getAsDouble() + k4.getAsDouble()));

    }
    public double calcK(double step, double der) {
        return step * der;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public OptionalDouble calcK(double step, OptionalDouble derivative) {
        if (derivative.isEmpty()) {
            return OptionalDouble.empty();
        }

        return OptionalDouble.of(calcK(step, derivative.getAsDouble()));
    }
}
