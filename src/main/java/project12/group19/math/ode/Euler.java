package project12.group19.math.ode;

import java.util.OptionalDouble;
import java.util.function.DoubleFunction;

public class Euler implements ODESolver{
    @Override
    public OptionalDouble apply(double value, double position, double step, DoubleFunction<OptionalDouble> derivative) {
        return OptionalDouble.of(value + derivative.apply(position).getAsDouble() * step);
    }

    public static void main(String[] args) {
        double x = 0;
        double y = 0;
        DoubleFunction<OptionalDouble> derivative = xc -> OptionalDouble.of(xc * 2);
        Euler solver = new Euler();
        for (int i = 0; i < 500; i++) {
            y = solver.apply(y, x, 0.01, derivative).getAsDouble();
            x += 0.01;
        }
        System.out.printf("x = %.2f, y = %.2f\\n", x, y);
    }
}
