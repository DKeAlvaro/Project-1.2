package project12.group19.api.math.differential;

import project12.group19.math.UnaryOperation;

import java.util.OptionalDouble;

public interface DerivativeCalculator {
    OptionalDouble estimate(UnaryOperation function, double position);
}
