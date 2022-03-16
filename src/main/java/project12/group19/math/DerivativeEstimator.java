package project12.group19.math;

import java.util.OptionalDouble;

public class DerivativeEstimator {
    private final double step;

    public DerivativeEstimator(double step) {
        this.step = step;
    }

    public OptionalDouble estimate(Subject subject, double position) {
        return estimate(subject, position, step);
    }

    public static OptionalDouble estimate(Subject subject, double position, double step) {
        OptionalDouble left = subject.getValue(position - step);
        OptionalDouble right = subject.getValue(position + step);

        if (left.isEmpty() && right.isEmpty()) {
            return OptionalDouble.empty();
        }

        if (left.isPresent() && right.isPresent()) {
            return OptionalDouble.of((right.getAsDouble() - left.getAsDouble()) / (2 * step));
        }

        // only of values is defined

        OptionalDouble current = subject.getValue(position);

        if (current.isEmpty()) {
            return OptionalDouble.empty();
        }

        OptionalDouble first = left.isPresent() ? left : current;
        OptionalDouble second = right.isPresent() ? right : current;
        return OptionalDouble.of((second.getAsDouble() - first.getAsDouble()) / step);
    }

    /**
     * A one-argument function that may be defined (or not) at specific
     * position. In case it is not defined an empty double should be
     * returned.
     */
    public interface Subject {
        OptionalDouble getValue(double position);
    }
}
