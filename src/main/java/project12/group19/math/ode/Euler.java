package project12.group19.math.ode;

import project12.group19.math.BinaryOperation;

import java.util.OptionalDouble;

public class Euler implements ODESolver{
    @Override
    public OptionalDouble apply(double value, double position, double step, BinaryOperation derivative) {
        OptionalDouble evaluation = derivative.apply(position, value);

        if (evaluation.isEmpty()) {
            return OptionalDouble.empty();
        }

        return OptionalDouble.of(value + evaluation.getAsDouble() * step);
    }

    public static void main(String[] args) {
        double x = 0;
        double y = 0;
        BinaryOperation derivative = (xc, any) -> OptionalDouble.of(xc * 2);
        Euler solver = new Euler();
        for (int i = 0; i < 500; i++) {
            y = solver.apply(y, x, 0.01, derivative).getAsDouble();
            x += 0.01;
        }
        System.out.printf("x = %.2f, y = %.2f\\n", x, y);
    }
}
