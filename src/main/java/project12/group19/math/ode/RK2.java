package project12.group19.math.ode;

import project12.group19.math.BinaryOperation;

import java.util.OptionalDouble;

public class RK2 implements ODESolver {
    private static final double TWO_THIRDS = 2.0 / 3.0;
    @Override
    public OptionalDouble apply(double value, double position, double step, BinaryOperation derivative) {
        OptionalDouble d1 = derivative.apply(position, value);
        OptionalDouble k1 = calcK(step, d1);

        if (k1.isEmpty()) {
            return OptionalDouble.empty();
        }

        OptionalDouble d2 = derivative.apply(position + TWO_THIRDS * step, value + TWO_THIRDS * k1.getAsDouble());
        OptionalDouble k2 = calcK(step, d2);

        if (k2.isEmpty()) {
            return OptionalDouble.empty();
        }

        return OptionalDouble.of(value + (1/4.0) * k1.getAsDouble() + (3/4.0) * k2.getAsDouble());
    }
    public double calcK(double step, double der) {
        return step * der;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public OptionalDouble calcK(double step, OptionalDouble derivative) {
        return derivative.isEmpty() ? OptionalDouble.empty() : OptionalDouble.of(calcK(step, derivative.getAsDouble()));
    }

    public static void main(String[] args) {
        double x = 0;
        double y = 0;
        BinaryOperation derivative = (xc, any) -> OptionalDouble.of(xc * 2);
        RK2 solver = new RK2();
        for (int i = 0; i < 500; i++) {
            y = solver.apply(y, x, 0.01, derivative).getAsDouble();
            x += 0.01;
        }
        System.out.printf("x = %.2f, y = %.2f\\n", x, y);
    }
}
