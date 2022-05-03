package project12.group19.math.parser.expression;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import project12.group19.math.parser.BinaryOperatorDefinition;
import project12.group19.math.parser.expression.component.BinaryOperator;
import project12.group19.math.parser.expression.component.Component;
import project12.group19.math.parser.expression.component.Value;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostfixExpressionTest {
    @Test
    public void correctlyHandlesUndefinedExpressions() {
        PostfixExpression sut = new PostfixExpression(
                List.of(
                        Value.of(1),
                        Value.of(0),
                        new BinaryOperator("/", BinaryOperatorDefinition.Standard.DIVIDE)
                ),
                "1/0"
        );

        assertThat(sut.calculate().isPresent(), is(false));
    }

    public static Stream<Arguments> invalidSequences() {
        return Stream.of(
                Arguments.of(List.of(Value.of(1), Value.of(1))),
                Arguments.of(List.of(
                        Value.of(1),
                        new BinaryOperator("+", BinaryOperatorDefinition.Standard.ADD),
                        Value.of(0)
                ))
        );
    }

    @MethodSource("invalidSequences")
    @ParameterizedTest
    public void throwsOnInvalidSequence(List<Component> list) {
        assertThrows(IllegalArgumentException.class, () -> new PostfixExpression(list).calculate());
    }
}
