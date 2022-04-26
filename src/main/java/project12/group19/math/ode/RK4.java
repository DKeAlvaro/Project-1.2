package project12.group19.math.ode;

import java.util.OptionalDouble;
import java.util.function.DoubleFunction;

public class RK4 implements ODESolver{
    @Override
    public OptionalDouble apply(double value, double position, double step, DoubleFunction<OptionalDouble> derivative) {
        double k1= calcK(step, derivative.apply(position).getAsDouble());
        double k2= calcK(step,  derivative.apply(position + (1.0/2.0)* step).getAsDouble());
        double k3= calcK(step,  derivative.apply(position + (1.0/2.0)* step).getAsDouble());
        double k4= calcK(step,  derivative.apply(position + step).getAsDouble());
        return OptionalDouble.of(value + (1/6.0) * (k1+ 2.0 * k2+ 2.0*k3 + k4));

    }
    public double calcK(double step, double der) {
        return step * der;
    }
    public static void main(String[] args) {
        double x = -5;
        double y = 0;
        DoubleFunction<OptionalDouble> derivative = xc -> OptionalDouble.of(xc * 2);
        RK4 solver = new RK4();
        for (int i = 0; i < 500; i++) {
            y = solver.apply(y, x, 0.01, derivative).getAsDouble();
            x += 0.01;
        }
        System.out.printf("x = %.2f, y = %.2f\\n", x, y);
    }
}
