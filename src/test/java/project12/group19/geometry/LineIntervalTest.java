package project12.group19.geometry;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import project12.group19.api.geometry.plane.PlanarCoordinate;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

class LineIntervalTest {
    public static Stream<Arguments> getPointPositiveSamples() {
        return Stream.of(
                Arguments.of(new LineInterval(PlanarCoordinate.origin(), PlanarCoordinate.origin()), 1, PlanarCoordinate.origin()),
                Arguments.of(new LineInterval(PlanarCoordinate.origin(), PlanarCoordinate.origin()), 2, PlanarCoordinate.origin()),
                Arguments.of(new LineInterval(PlanarCoordinate.origin(), PlanarCoordinate.origin()), 0, PlanarCoordinate.origin()),
                Arguments.of(new LineInterval(PlanarCoordinate.origin(), PlanarCoordinate.origin()), -1, PlanarCoordinate.origin()),
                Arguments.of(new LineInterval(PlanarCoordinate.origin(), PlanarCoordinate.create(1, 1)), 1, PlanarCoordinate.create(1, 1)),
                Arguments.of(new LineInterval(PlanarCoordinate.origin(), PlanarCoordinate.create(1, 1)), 2, PlanarCoordinate.create(2, 2)),
                Arguments.of(new LineInterval(PlanarCoordinate.origin(), PlanarCoordinate.create(1, 1)), -2, PlanarCoordinate.create(-2, -2)),
                Arguments.of(new LineInterval(PlanarCoordinate.origin(), PlanarCoordinate.create(1, 1)), 0.5, PlanarCoordinate.create(0.5, 0.5)),
                Arguments.of(new LineInterval(PlanarCoordinate.create(2, 5), PlanarCoordinate.create(5, 2)), 0.5, PlanarCoordinate.create(3.5, 3.5))
        );
    }

    public static Stream<Arguments> lengthSamples() {
        return Stream.of(
                Arguments.of(new LineInterval(PlanarCoordinate.origin(), PlanarCoordinate.origin()), 0, 0, 0),
                Arguments.of(new LineInterval(PlanarCoordinate.origin(), PlanarCoordinate.create(0, 1)), 0, 1, 1),
                Arguments.of(new LineInterval(PlanarCoordinate.origin(), PlanarCoordinate.create(1, 0)), 1, 0, 1),
                Arguments.of(new LineInterval(PlanarCoordinate.origin(), PlanarCoordinate.create(1, 1)), 1, 1, Math.sqrt(2)),
                Arguments.of(new LineInterval(PlanarCoordinate.origin(), PlanarCoordinate.create(1, 1)), 1, 1, Math.sqrt(2)),
                Arguments.of(new LineInterval(PlanarCoordinate.create(1, 2), PlanarCoordinate.create(4, 6)), 3, 4, 5)
        );
    }

    @ParameterizedTest
    @MethodSource("getPointPositiveSamples")
    public void getsCorrectPoint(LineInterval interval, double offset, PlanarCoordinate expectation) {
        PlanarCoordinate point = interval.getPoint(offset);
        assertThat("Point " + point + " is not within marginal distance of " + offset, point.isCloseTo(expectation, 1E-6));
    }

    @ParameterizedTest
    @MethodSource("lengthSamples")
    public void calculatesCorrectLength(LineInterval interval, double xLength, double yLength, double absoluteLength) {
        assertThat(interval.getXLength(), closeTo(xLength, 1E-6));
        assertThat(interval.getYLength(), closeTo(yLength, 1E-6));
        assertThat(interval.getLength(), closeTo(absoluteLength, 1E-6));
    }
}
