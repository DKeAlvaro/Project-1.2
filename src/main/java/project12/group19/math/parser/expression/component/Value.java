package project12.group19.math.parser.expression.component;

import java.util.Stack;

public record Value(String source, double identity) implements Component {
    @Override
    public String getSource() {
        return source;
    }

    @Override
    public boolean process(Stack<Double> stack) {
        stack.push(identity);

        return true;
    }

    public static Value of(double identity) {
        return new Value(Double.toString(identity), identity);
    }
}
