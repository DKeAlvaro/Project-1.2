package project12.group19.math.parser.expression.component;

import project12.group19.math.parser.OperationDefinition;

import java.util.OptionalDouble;
import java.util.Stack;

public record Operation(String source, OperationDefinition definition) implements Component {
    @Override
    public String getSource() {
        return source;
    }

    @Override
    public boolean process(Stack<Double> stack) {
        if (stack.size() < definition.getArity()) {
            throw new IllegalStateException("Less arguments left on stack than required for operation " + source);
        }

        double[] arguments = new double[definition.getArity()];

        for (int i = 0; i < definition.getArity(); i++) {
            arguments[definition.getArity() - i - 1] = stack.pop();
        }

        OptionalDouble result = definition.apply(arguments);

        result.ifPresent(stack::push);

        return result.isPresent();
    }
}
