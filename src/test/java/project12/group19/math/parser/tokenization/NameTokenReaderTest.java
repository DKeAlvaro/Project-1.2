package project12.group19.math.parser.tokenization;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

class NameTokenReaderTest {
    private static final TokenReader<String> SUT = new NameTokenReader();

    public static Stream<Arguments> variables() {
        return Stream.of(
                Arguments.of("sin", 0, "sin"),
                Arguments.of("  sin", 2, "sin"),
                Arguments.of("  sin  ", 2, "sin"),
                Arguments.of("x.k", 0, "x.k"),
                Arguments.of("(y1)", 1, "y1"),
                Arguments.of("x", 0, "x")
        );
    }

    public static Stream<Arguments> nonNames() {
        return Stream.of(
                Arguments.of("12", 0),
                Arguments.of("   ", 0),
                Arguments.of("sin  ", 3),
                Arguments.of("_", 0),
                Arguments.of(")", 0),
                Arguments.of("*", 0),
                Arguments.of("12.34E+12", 0),
                Arguments.of(" x", 0)
        );
    }

    public static Stream<Arguments> functions() {
        return Stream.of(
                Arguments.of("  Math.sin   (", 2, "Math.sin")
        );
    }

    @ParameterizedTest
    @MethodSource("variables")
    public void positive(String input, int offset, String expectation) {
        Token<?> token = SUT.read(input, offset, List.of());
        assertThat(token, notNullValue());
        assertThat(token.offset(), equalTo(offset));
        assertThat(token.kind(), equalTo(Token.Kind.VARIABLE));
        assertThat(token.source(), equalTo(expectation));
    }

    @ParameterizedTest
    @MethodSource("nonNames")
    public void negative(String input, int offset) {
        Token<?> token = SUT.read(input, offset, List.of());
        assertThat(token, nullValue());
    }

    @ParameterizedTest
    @MethodSource("functions")
    public void positiveFunctions(String input, int offset, String expectation) {
        Token<?> token = SUT.read(input, offset, List.of());
        assertThat(token, notNullValue());
        assertThat(token.offset(), equalTo(offset));
        assertThat(token.source(), equalTo(expectation));
        assertThat(token.kind(), equalTo(Token.Kind.FUNCTION));
    }
}
