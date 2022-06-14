package project12.group19.api.physics;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

class VelocityTest {
    public static Stream<Arguments> angleInputs() {
        return Stream.of(
                Arguments.of(1.0, 0.0, 0),
                Arguments.of(1.0, 1.0, Math.PI / 4),
                Arguments.of(0.0, 1.0, Math.PI / 2),
                Arguments.of(-1.0, 1.0, Math.PI * 3 / 4),
                Arguments.of(-1.0, 1E-6, Math.PI - 1E-6),
                Arguments.of(-1.0, -1E-6, -Math.PI + 1E-6),
                Arguments.of(-1.0, -1.0, -Math.PI * 3 / 4),
                Arguments.of(0.0, -1.0, -Math.PI / 2),
                Arguments.of(1.0, -1.0, -Math.PI / 4)
        );
    }
    @ParameterizedTest
    @MethodSource("angleInputs")
    public void providesCorrectAngle(double x, double y, double expectation) {
        assertThat(Velocity.create(x, y).getVelocityAngle(), closeTo(expectation, 1E-6));
    }
}
