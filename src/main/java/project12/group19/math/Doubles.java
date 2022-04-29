package project12.group19.math;

import java.util.OptionalDouble;
import java.util.function.DoubleUnaryOperator;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class Doubles {
    public static OptionalDouble map(OptionalDouble container, DoubleUnaryOperator transformer) {
        if (container.isEmpty()) {
            return OptionalDouble.empty();
        }

        return OptionalDouble.of(transformer.applyAsDouble(container.getAsDouble()));
    }
}
