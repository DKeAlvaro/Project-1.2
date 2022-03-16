package project12.group19.math.parser.expression;

import org.junit.jupiter.api.Test;
import project12.group19.math.parser.OperatorDefinition;
import project12.group19.math.parser.expression.component.Operator;
import project12.group19.math.parser.expression.component.Value;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class InfixExpressionTest {
    @Test
    public void correctlyHandlesUndefinedExpressions() {
        InfixExpression sut = new InfixExpression(
                "1/0",
                List.of(
                        Value.of(1),
                        Value.of(0),
                        new Operator("/", OperatorDefinition.Standard.DIVIDE)
                )
        );

        assertThat(sut.calculate().isPresent(), is(false));
    }
}
