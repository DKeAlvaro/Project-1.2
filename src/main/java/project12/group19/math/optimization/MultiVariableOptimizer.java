package project12.group19.math.optimization;

import project12.group19.math.MultiParameterOperation;

import java.util.List;
import java.util.Optional;

/**
 * This interface describes an optimizer of input for function, such as
 * hill climbing.
 *
 * It is implied that some external iterative process is responsible for
 * providing the initial parameters, controlling number of iterations,
 * checking whether optimization is going the correct way, and so on;
 * the goal of optimizer is to only give the next guess based on
 * previous history.
 */
public interface MultiVariableOptimizer {
    /**
     * Performs a single round of optimization.
     *
     * @param parameters List of variable values for a function.
     * @param function Function that is the subject of estimation.
     * @param goal Target function value.
     * @param history List of previous optimizations.
     * @param precision A proportion at which solver should try to
     * optimize parameters. 1.0 means "normal" optimization (for some
     * optimizers it would mean "project reaching the goal in one
     * shot"), 0.5 would stand for half as much optimization ("travel
     * half the path to the goal"), 0.25 would result in quarter of
     * that, and so on.
     * @return Optimized set of parameters. If the optimizer can't
     * provide any optimization for whatever reasons, it should return
     * empty optional.
     */
    Optional<double[]> optimize(
            MultiParameterOperation function,
            double[] parameters,
            double goal,
            List<Round> history,
            double precision
    );

    interface Round {
        double[] getPreviousParameters();
        double[] getOptimizedParameters();
        default double[] getParameterDifferences() {
            double[] previous = getPreviousParameters();
            double[] optimized = getOptimizedParameters();
            double[] result = new double[previous.length];

            for (int i = 0; i < previous.length; i++) {
                result[i] = optimized[i] - previous[i];
            }

            return result;
        }
        double getPreviousValue();
        double getOptimizedValue();
        double getValueDifference();
    }
}
