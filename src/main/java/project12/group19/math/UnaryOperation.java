package project12.group19.math;

import java.util.OptionalDouble;

@FunctionalInterface
public interface UnaryOperation {
    OptionalDouble apply(double value);
}
