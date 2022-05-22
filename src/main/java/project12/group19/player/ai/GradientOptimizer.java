package project12.group19.player.ai;

import project12.group19.math.DerivativeEstimator;
import project12.group19.math.optimization.MultiVariableOptimizer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.ToDoubleFunction;

public class GradientOptimizer implements MultiVariableOptimizer {
    private static final DerivativeEstimator DERIVATIVE_ESTIMATOR = new DerivativeEstimator(1E-9);

    @Override
    public Optional<double[]> optimize(double[] parameters, ToDoubleFunction<double[]> function, double goal, List<Round> history, double precision) {
        double[] gradient = new double[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            OptionalDouble partialDerivative = getPartialDerivative(function, parameters, i);

            if (partialDerivative.isEmpty()) {
                return Optional.empty();
            }

            gradient[i] = partialDerivative.getAsDouble();
        }

        double distance = Math.abs(function.applyAsDouble(parameters) - goal);
        normalize(gradient);
        multiply(gradient, distance/length(gradient));
        multiply(gradient, -precision);
        return Optional.of(sum(parameters, gradient));
    }

    private static void normalize(double[] vector) {
        double length = length(vector);
        for (int i = 0; i < vector.length; i++) {
            vector[i] /= length;
        }
    }

    private static double length(double[] vector) {
        double accumulator = 0;

        for (double element : vector) {
            accumulator += element * element;
        }

        return Math.sqrt(accumulator);
    }

    private static void multiply(double[] values, double multiplier) {
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i] * multiplier;
        }
    }

    private static double[] sum(double[] alpha, double[] beta) {
        double[] result = Arrays.copyOf(alpha, alpha.length);
        for (int i = 0; i < beta.length; i++) {
            result[i] += beta[i];
        }
        return result;
    }

    private static OptionalDouble getPartialDerivative(ToDoubleFunction<double[]> function, double[] parameters, int index) {
        double position = parameters[index];
        double[] input = Arrays.copyOf(parameters, parameters.length);
        return DERIVATIVE_ESTIMATOR.estimate(variable -> {
            input[index] = variable;
            return OptionalDouble.of(function.applyAsDouble(input));
        }, position);
    }

    public static void main(String[] args) {
        ToDoubleFunction<double[]> fn = input -> {
            double x = input[0];
            double y = input[1];
            return Math.pow(x - 3, 2) + Math.pow(y + 2, 3);
        };

        GradientOptimizer optimizer = new GradientOptimizer();
        double[] parameters = new double[] { 0, 0 };
        for (int i = 0; i < 1000; i++) {
            Optional<double[]> optimized = optimizer.optimize(parameters, fn, 0, List.of(), 0.05);
            System.out.println("Iteration " + i);
            if (optimized.isEmpty()) {
                System.out.println("Failed to optimize");
                break;
            }

            parameters = optimized.get();
            System.out.println("Optimized values: " + Arrays.toString(parameters));
        }
    }
}
