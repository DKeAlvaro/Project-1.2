package project12.group19.api.math.differential;

import project12.group19.math.BinaryOperation;

import java.util.OptionalDouble;

/**
 * An extension to {@link ODESolver} that uses history of previous
 * approximations to provide new one.
 *
 * See <a href="https://en.wikipedia.org/wiki/Linear_multistep_method">wikipedia</a>
 * for linear multistep methods.
 */
public interface MultiStepODESolver {
    /**
     * Provides next approximation at {@code position + step} given
     * function value at {@code position}.
     *
     * @param value Current function value, in classic ODE terminology
     * that would be y_n (w_n).
     * @param position Current position / variable value, in classic ODE
     * terminology that would be t_n.
     * @param step Step size at which {@code position + step size}
     * @param derivative A function to evaluate derivative at specific
     * point ({@code f(t, y)}).
     * @param history Previous calculations.
     * @return Next prediction. If one of the necessary values during
     * computation turns out to be undefined, returns empty optional.
     */
    OptionalDouble apply(double value, double position, double step, BinaryOperation derivative, double[] history);

    /**
     * @return Number of steps in history used. Client code should not
     * keep more steps than that.
     */
    int getHistoryLength();
}
