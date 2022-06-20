package project12.group19.math.differential;

import project12.group19.api.math.differential.DerivativeCalculator;
import project12.group19.math.UnaryOperation;

import java.util.OptionalDouble;

/**
 * TODO: rename according to proper numerical math implementation
 */
public class DerivativeEstimator implements DerivativeCalculator {
    private final double step;

    public DerivativeEstimator(double step) {
        this.step = step;
    }

    public OptionalDouble estimate(UnaryOperation subject, double position) {
        return estimate(subject, position, step);
    }

    public static OptionalDouble estimate(UnaryOperation subject, double position, double step) {
        OptionalDouble left = subject.apply(position - step);
        OptionalDouble right = subject.apply(position + step);

        if (left.isEmpty() && right.isEmpty()) {
            return OptionalDouble.empty();
        }

        if (left.isPresent() && right.isPresent()) {
            return OptionalDouble.of((right.getAsDouble() - left.getAsDouble()) / (2 * step));
        }

        // only one of values is defined

        OptionalDouble current = subject.apply(position);

        if (current.isEmpty()) {
            return OptionalDouble.empty();
        }

        OptionalDouble first = left.isPresent() ? left : current;
        OptionalDouble second = right.isPresent() ? right : current;
        return OptionalDouble.of((second.getAsDouble() - first.getAsDouble()) / step);
    }
}
