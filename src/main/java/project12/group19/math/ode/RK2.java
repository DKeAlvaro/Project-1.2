package project12.group19.math.ode;

import java.util.OptionalDouble;
import java.util.function.DoubleFunction;

public class RK2 implements ODESolver{
    @Override
    public OptionalDouble apply(double value, double position, double step, DoubleFunction<OptionalDouble> derivative) {
        double k1= calcK(step, derivative.apply(position).getAsDouble());
        double k2= calcK(step,  derivative.apply(position + (2.0/3.0)* step).getAsDouble());
        return OptionalDouble.of(value + (1/4.0) * k1+ (3/4.0) * k2);
    }
    public double calcK(double step, double der) {
        return step * der;
    }
    public static void main(String[] args) {
        double x = 0;
        double y = 0;
        DoubleFunction<OptionalDouble> derivative = xc -> OptionalDouble.of(xc * 2);
        RK2 solver = new RK2();
        for (int i = 0; i < 500; i++) {
            y = solver.apply(y, x, 0.01, derivative).getAsDouble();
            x += 0.01;
        }
        System.out.printf("x = %.2f, y = %.2f\\n", x, y);
    }
}
