package project12.group19;

import project12.group19.math.BinaryOperation;
import project12.group19.math.ode.Euler;
import project12.group19.math.ode.ODESolver;
import project12.group19.math.ode.RK2;
import project12.group19.math.ode.RK4;

import java.util.Collection;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Solvers {
    private static final List<ODESolver> SOLVERS = List.of(new Euler(), new RK4(), new RK2());
    private static final List<Double> STEPS = List.of(0.1, 0.04, 0.07, 0.01, 0.003,0.007, 0.001, 0.0003, 0.0007, 0.0001, 0.00001, 0.000001, 0.00000001);

    private record Round(
            String solver,
            double step,
            long iterations,
            double value,
            double estimation,
            double absoluteError,
            double relativeError
    ) {}

    private static Round run(ODESolver solver, double y, double t, double tMax, double step, BinaryOperation derivative, double exact) {
        long iterations = 0;
        double estimation = y;
        double tCurrent = t;

        for (iterations = 0; iterations < tMax/step; iterations++) {
            estimation = solver.apply(estimation, tCurrent, step, derivative).getAsDouble();
            tCurrent += step;
        }

//        for (tCurrent = t; tCurrent < tMax; tCurrent += step) {
//            iterations++;
//            estimation = solver.apply(estimation, tCurrent, step, derivative).getAsDouble();
//        }

        double absoluteError = Math.abs(estimation - exact);
        double relativeError = Math.abs(absoluteError / exact);
        return new Round(
                solver.getClass().getSimpleName(),
                step,
                iterations,
                exact,
                estimation,
                absoluteError,
                relativeError
        );
    }

    private static Stream<Round> run(ODESolver solver, double y, double t, double tMax, Collection<Double> steps, BinaryOperation derivative, double exact) {
        return steps.stream().map(step -> run(solver, y, t, tMax, step, derivative, exact));
    }

    private static Stream<Round> run(
            Collection<ODESolver> solvers,
            double y,
            double t,
            double tMax,
            Collection<Double> steps,
            BinaryOperation derivative,
            double exact
    ) {
        return solvers.stream().flatMap(solver -> run(solver, y, t, tMax, steps, derivative, exact));
    }

    private static Stream<Round> run(double y, double t, double tMax, BinaryOperation derivative, double exact) {
        return run(SOLVERS, y, t, tMax, STEPS, derivative, exact);
    }

    public static void main(String[] args) {
//        ToDoubleFunction<Double> exact = y -> Math.pow(Math.E, y/2) - 8;
//        run(8.1, 0, 10, (t, y) -> OptionalDouble.of(t/2 - 4), exact.applyAsDouble(10.0)).forEach(round ->
//                System.out.printf("%s: %.7f %f\n", round.solver, round.step, round.relativeError)
//        );

//        ToDoubleFunction<Double> exact = t -> Math.pow(t, 5) / 5;
//        BinaryOperation derivative = (t, y) -> OptionalDouble.of(Math.pow(t, 4));

        ToDoubleFunction<Double> exact = t -> 0.1 * Math.exp(0.5*t) + 8; //correct

        BinaryOperation derivative = (t, y) -> OptionalDouble.of(0.5 * y - 4);

        run(8.1, 0, 10, derivative, exact.applyAsDouble(10.0)).forEach(round ->
                System.out.printf("%s: step=%.7f result=%.12f relative error=%.18f\n", round.solver, round.step, round.estimation, round.relativeError)
        );
    }

    static class InlineRK2 implements ODESolver {
        @Override
        public OptionalDouble apply(double value, double position, double step, BinaryOperation derivative) {
//            double k1 = step * derivative.apply(position, value).getAsDouble();
//            double k2 = step * derivative.apply(position * 2.0/3.0, value + k1 * 2.0/3.0).getAsDouble();
            double k1 = step * derivative(value);
            double k2 = step * derivative(value + k1 * 2.0/3.0);
            return OptionalDouble.of(value + ((1.0/4.0) * (k1 + 3.0 * k2)));
        }

        private double derivative(double value) {
            return 0.5 * value - 4;
        }
    }
}
