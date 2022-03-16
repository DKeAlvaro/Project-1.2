package project12.group19.math;

import java.util.OptionalDouble;

@FunctionalInterface
public interface BinaryOperation {
    OptionalDouble apply(double alpha, double beta);
}
