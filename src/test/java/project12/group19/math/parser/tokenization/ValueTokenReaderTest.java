package project12.group19.math.parser.tokenization;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

class ValueTokenReaderTest {
    private static final TokenReader<Double> SUT = new ValueTokenReader();

    public static Stream<Arguments> values() {
        return Stream.of(
                Arguments.of("3", 0, 3),
                Arguments.of("3.1", 0, 3.1),
                Arguments.of("3E+2", 0, 3E+2),
                Arguments.of("3.11E-02", 0, 3.11E-2),
                Arguments.of("Math.sin(3.10E-02)", 9, 3.10E-2)
        );
    }

    public static Stream<Arguments> nonValues() {
        return Stream.of(
                Arguments.of("name", 0),
                Arguments.of("3.1", 1),
                Arguments.of("E+2", 0),
                Arguments.of("*", 0)
        );
    }

    @ParameterizedTest
    @MethodSource("values")
    public void positive(String input, int offset, double expectation) {
        Token<Double> token = SUT.read(input, offset, List.of());
        assertThat(token, notNullValue());
        assertThat(token.offset(), equalTo(offset));
        assertThat(token.value(), closeTo(expectation, 1E-9));
    }

    @ParameterizedTest
    @MethodSource("nonValues")
    public void negative(String input, int offset) {
        assertThat(SUT.read(input, offset, List.of()), nullValue());
    }
}
