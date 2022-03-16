package project12.group19.math.parser.expression.component;

import java.util.Stack;

public record Variable(String name) implements Component {
    @Override
    public String getSource() {
        return name;
    }

    @Override
    public boolean process(Stack<Double> stack) {
        String message = "Unresolved variable " + name + " encountered. " +
                "Resolve all variables before calculating the expression";
        throw new IllegalStateException(message);
    }
}
