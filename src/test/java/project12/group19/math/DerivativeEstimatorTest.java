package project12.group19.math;

import org.hamcrest.Matcher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import project12.group19.math.differential.DerivativeEstimator;

import java.util.OptionalDouble;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

class DerivativeEstimatorTest {
    private static final double STEP = 1E-9;
    private static final double ERROR = 1E-3;
    private static final Matcher<Double> MATCHER = closeTo(1, ERROR);
    private static final DerivativeEstimator SUT = new DerivativeEstimator(STEP);

    private static Stream<Arguments> degrees() {
        return IntStream.range(0, 360)
                // removing edge cases
                .filter(x -> x % 90 != 0)
                .mapToObj(Arguments::of);
    }

    private static Stream<Arguments> doubles() {
        return DoubleStream
                .concat(
                        DoubleStream.iterate(-100, x -> x < 100, x -> x += 0.31),
                        DoubleStream.iterate(-10000, x -> x < 10000, x -> x += 57.31)
                ).mapToObj(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("degrees")
    public void testSineDerivative(int degrees) {
        double radians = Math.toRadians(degrees);
        UnaryOperation subject = x -> OptionalDouble.of(Math.sin(x));

        OptionalDouble estimate = SUT.estimate(subject, radians);
        assertThat("Sine derivative is undefined", estimate.isPresent());
        assertThat(estimate.getAsDouble() / Math.cos(radians), MATCHER);
    }

    @ParameterizedTest
    @MethodSource("degrees")
    public void testCosineDerivative(int degrees) {
        double radians = Math.toRadians(degrees);
        UnaryOperation subject = x -> OptionalDouble.of(Math.cos(x));

        OptionalDouble estimate = SUT.estimate(subject, radians);
        assertThat("Cosine derivative is undefined", estimate.isPresent());
        assertThat(estimate.getAsDouble() / -Math.sin(radians), MATCHER);
    }

    @ParameterizedTest
    @MethodSource("doubles")
    public void testSquareDerivative(double x) {
        UnaryOperation subject = v -> OptionalDouble.of(v * v);

        OptionalDouble estimate = SUT.estimate(subject, x);
        assertThat("Parabola derivative is undefined", estimate.isPresent());
        assertThat(estimate.getAsDouble() / (2 * x), MATCHER);
    }
}
