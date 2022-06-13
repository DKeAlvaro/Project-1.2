package project12.group19;

import project12.group19.math.BinaryOperation;
import project12.group19.math.ode.Euler;
import project12.group19.math.ode.ODESolver;
import project12.group19.math.ode.RK2;
import project12.group19.math.ode.RK4;

import java.util.Collection;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Solvers {
    private static final List<ODESolver> SOLVERS = List.of(new Euler(), new RK4(), new RK2());
    private static final List<Double> STEPS = List.of(0.1, 0.1, 0.05, 0.01, 0.005, 0.001, 0.0005,   0.0001, 0.00001, 0.000001, 0.0000001, 0.00000001);
    private static final long MINIMUM_RUN_TIME = 1_000_000_000L; // 5 second in nanoseconds

    private record Round(
            String solver,
            double step,
            long iterations,
            double value,
            double estimation,
            double absoluteError,
            double relativeError,
            long time
    ) {}

    private static Round run(ODESolver solver, double y, double t, double tMax, double step, BinaryOperation derivative, double exact) {
        long iterations = 0;
        double estimation = y;
        double tCurrent = t;

        long timeStart = System.nanoTime();
        int outerIterations = 0;
        do {
            iterations = 0;
            estimation = y;
            tCurrent = t;
            for (iterations = 0; iterations < tMax / step; iterations++) {
                estimation = solver.apply(estimation, tCurrent, step, derivative).getAsDouble();
                tCurrent += step;
            }
            outerIterations++;
        } while (System.nanoTime() < timeStart + MINIMUM_RUN_TIME);
        long timeEnd = System.nanoTime();

        long time = timeEnd - timeStart;

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
                relativeError,
                time / outerIterations
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

        // warmup to let JIT kick in
        long duration = 10 * 1000; // ten seconds
        long start = System.currentTimeMillis();

        System.out.printf("Warming up for %.2f seconds...\n", duration / 1000.0);
        while (System.currentTimeMillis() < start + duration) {
            run(SOLVERS, 8.1, 0, 10, Set.of(0.001), derivative, exact.applyAsDouble(10.0));
        }
        System.out.println("Warm up done!");

        run(8.1, 0, 10, derivative, exact.applyAsDouble(10.0)).forEach(round ->
                System.out.printf("%s: step=%.8f result=%.5f error=%.18f time: %d\n", round.solver, round.step, round.estimation, round.relativeError, round.time)
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
