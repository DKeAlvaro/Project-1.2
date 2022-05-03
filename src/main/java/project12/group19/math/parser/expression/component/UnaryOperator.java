package project12.group19.math.parser.expression.component;

import project12.group19.math.parser.UnaryOperatorDefinition;

import java.util.OptionalDouble;
import java.util.Stack;

public record UnaryOperator(String source, UnaryOperatorDefinition definition) implements Component {
    @Override
    public String getSource() {
        return source;
    }

    @Override
    public boolean process(Stack<Double> stack) {
        double value = stack.pop();
        OptionalDouble evaluation = definition.getOperation().apply(value);

        evaluation.ifPresent(stack::push);

        return evaluation.isPresent();
    }
}
