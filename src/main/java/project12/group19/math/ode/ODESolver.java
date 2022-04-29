package project12.group19.math.ode;

import project12.group19.math.BinaryOperation;

import java.util.OptionalDouble;

/**
 * This interface describes a numerical solver of ordinary differential
 * equation, which, given a value of a function, value of variable,
 * derivative and step, approximates value of that function at variable
 * incremented by step.
 */
@FunctionalInterface
public interface ODESolver {
    /**
     * Returns approximated value of a function for variable set to
     * {@code position + step}.
     *
     * @param value Current function value.
     * @param position Current variable value.
     * @param step Defines difference to next variable value to be
     * approximated.
     * @param derivative Function that allows to get derivative value at
     * for specific value of variable. Since derivative isn't guaranteed
     * to be defined, it returns an {@link OptionalDouble}, which can be
     * empty (and implementations should account for that).
     * @return Approximated function value. Since it's possible for
     * value to be uncomputable, solver is allowed to return empty
     * {@link OptionalDouble} as well.
     */
    OptionalDouble apply(double value, double position, double step, BinaryOperation derivative);
}
