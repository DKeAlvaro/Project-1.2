package project12.group19.math.parser.expression.component;

import project12.group19.math.parser.BinaryOperatorDefinition;

import java.util.OptionalDouble;
import java.util.Stack;

public record BinaryOperator(String source, BinaryOperatorDefinition definition) implements Component {
    @Override
    public String getSource() {
        return source;
    }

    @Override
    public boolean process(Stack<Double> stack) {
        if (stack.size() < 2) {
            throw new IllegalArgumentException("Less than two variables left on stack for processing operator " + source);
        }

        double right = stack.pop();
        double left = stack.pop();

        OptionalDouble evaluation = definition.getOperation().apply(left, right);

        evaluation.ifPresent(stack::push);

        return evaluation.isPresent();
    }
}
