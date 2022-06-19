package project12.group19.math.parser.expression;

import project12.group19.math.parser.expression.component.Component;
import project12.group19.math.parser.expression.component.Value;
import project12.group19.math.parser.expression.component.Variable;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * TODO: implement {@link project12.group19.api.math.expression.Expression} interface
 */
public record PostfixExpression(List<Component> components, CharSequence source) {
    public PostfixExpression(List<Component> components) {
        this(components, null);
    }
    public OptionalDouble calculate() {
        Stack<Double> stack = new Stack<>();

        for (Component component : components) {
            if (!component.process(stack)) {
                return OptionalDouble.empty();
            }
        }

        if (stack.size() != 1) {
            String message = "Invalid expression input - calculating expression resulted in non-singular value on stack: " + stack;
            throw new IllegalArgumentException(message);
        }

        return OptionalDouble.of(stack.pop());
    }

    public PostfixExpression resolve(Map<String, Double> variables) {
        List<Component> resolved = components.stream()
                .map(component -> {
                    if (component instanceof Variable variable && variables.containsKey(variable.name())) {
                        return new Value(variable.getSource(), variables.get(variable.name()));
                    }

                    return component;
                })
                .collect(Collectors.toList());

        return new PostfixExpression(resolved, source);
    }

    public PostfixExpression resolve(String variable, double value) {
        return resolve(Map.of(variable, value));
    }
}
