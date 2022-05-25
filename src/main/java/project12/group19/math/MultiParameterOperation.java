package project12.group19.math;

import java.util.OptionalDouble;

/**
 * A generic interface for a function. It is implied that specific
 * function always has only fixed number of parameters, but some pieces
 * of code like derivation estimators or optimizers may work with
 * families of functions (for example, a generic optimizer will work the
 * same with 2-argument and 4-argument function). To facilitate such
 * pieces of code functions, this interface allows passing around
 * functions with different number of parameters as a single type.
 */
@FunctionalInterface
public interface MultiParameterOperation {
    OptionalDouble apply(double[] parameters);

    static MultiParameterOperation convert(UnaryOperation subject) {
        return parameters -> subject.apply(parameters[0]);
    }

    static MultiParameterOperation convert(BinaryOperation subject) {
        return parameters -> subject.apply(parameters[0], parameters[1]);
    }
}
