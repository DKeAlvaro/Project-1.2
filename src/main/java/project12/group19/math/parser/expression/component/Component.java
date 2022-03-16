package project12.group19.math.parser.expression.component;

import java.util.Stack;

public sealed interface Component permits Value, Variable, Operator, Operation {
    String getSource();
    boolean process(Stack<Double> stack);
}
