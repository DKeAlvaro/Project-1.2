package project12.group19.math.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import project12.group19.math.parser.component.ComponentRegistry;
import project12.group19.math.parser.expression.InfixExpression;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest {
    private static final Parser SUT = new Parser(ComponentRegistry.standard());

    public static Stream<Arguments> expressions() {
        return Stream.of(
                Arguments.of("3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3", Map.of(), 3 + (6.0/65536)),
                Arguments.of("sin ( max ( 2, 3 ) / 3 * pi )", Map.of("pi", Math.PI), 0),
                Arguments.of("0.5*(Math.sin((pi * (x-y))/7))+0.9", Map.of("x", 12.5, "y", 9.0, "pi", Math.PI), 1.4),
                Arguments.of("-0.5*-(--Math.sin((pi * (x-y))/7))+++0.9", Map.of("x", 12.5, "y", 9.0, "pi", Math.PI), 1.4)
        );
    }

    @ParameterizedTest
    @MethodSource("expressions")
    public void positive(String expression, Map<String, Double> variables, double expectation) {
        InfixExpression parsed = SUT.parse(expression);
        OptionalDouble calculation = parsed.resolve(variables).calculate();
        assertThat(calculation.isPresent(), is(true));
        assertThat(calculation.getAsDouble(), closeTo(expectation, 1E-3));
    }

    @Test
    public void unexpectedOperation() {
        assertThrows(IllegalArgumentException.class, () -> SUT.parse("multimax(2,3,4)"));
    }
}
