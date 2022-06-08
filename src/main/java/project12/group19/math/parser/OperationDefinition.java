package project12.group19.math.parser;

import java.util.OptionalDouble;
import java.util.Set;

public interface OperationDefinition {
    String getName();
    Set<String> getAliases();
    int getArity();
    OptionalDouble apply(double[] arguments);

    record Standard(String name, int arity, Executor executor, Set<String> aliases) implements OperationDefinition {
        public static final OperationDefinition SINE = new Standard(
                "sin",
                1,
                arguments -> OptionalDouble.of(Math.sin(arguments[0])),
                Set.of("sine", "Math.sin", "Math.sine")
        );

        public static final OperationDefinition COSINE = new Standard(
                "cos",
                1,
                arguments -> OptionalDouble.of(Math.cos(arguments[0])),
                Set.of("cosine", "Math.cos", "Math.cosine")
        );

        public static final OperationDefinition ABSOLUTE = new Standard(
                "abs",
                1,
                arguments -> OptionalDouble.of(Math.abs(arguments[0])),
                Set.of("absolute", "Math.abs", "Math.absolute")
        );
        
        public static final OperationDefinition MAXIMUM = new Standard(
                "max",
                2,
                arguments -> OptionalDouble.of(Math.max(arguments[0], arguments[1])),
                Set.of("maximum", "Math.max", "Math.maximum")
        );
        
        public static final OperationDefinition MINIMUM = new Standard(
                "min",
                2,
                arguments -> OptionalDouble.of(Math.min(arguments[0], arguments[1])),
                Set.of("minimum", "Math.min", "Math.minimum")
        );

        public static final OperationDefinition LOGARITHM = new Standard(
                "log",
                2,
                arguments -> {
                    double base = Math.log(arguments[1]);

                    if (base == 0) {
                        return OptionalDouble.empty();
                    }

                    // base switching
                    return OptionalDouble.of(Math.log(arguments[0]) / base);
                },
                Set.of("logarithm", "Math.log", "Math.logarithm")
        );

        public static final OperationDefinition NATURAL_LOGARITHM = new Standard(
                "ln",
                1,
                arguments -> arguments[0] <= 0 ? OptionalDouble.empty() : OptionalDouble.of(Math.log(arguments[0])),
                Set.of("Math.ln")
        );

        public static final OperationDefinition BASE_TEN_LOGARITHM = new Standard(
                "log10",
                1,
                arguments -> arguments[0] <= 0 ? OptionalDouble.empty() : OptionalDouble.of(Math.log10(arguments[0])),
                Set.of("Math.log10")
        );

        public static final OperationDefinition BASE_TWO_LOGARITHM = new Standard(
                "log2",
                1,
                arguments -> arguments[0] <= 0 ? OptionalDouble.empty() : OptionalDouble.of(Math.log(arguments[0]) / Math.log(2)),
                Set.of("Math.log2")
        );

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Set<String> getAliases() {
            return aliases;
        }

        @Override
        public int getArity() {
            return arity;
        }

        @Override
        public OptionalDouble apply(double[] arguments) {
            return executor().apply(arguments);
        }

        interface Executor {
            OptionalDouble apply(double[] arguments);
        }
    }
}
