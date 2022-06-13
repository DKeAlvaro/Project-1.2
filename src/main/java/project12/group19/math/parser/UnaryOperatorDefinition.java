package project12.group19.math.parser;

import project12.group19.math.UnaryOperation;

import java.util.OptionalDouble;
import java.util.Set;

public interface UnaryOperatorDefinition {
    String getName();
    Set<String> getAliases();
    UnaryOperation getOperation();
    int getPrecedence();

    record Standard(String name, Set<String> aliases, UnaryOperation operation, int precedence) implements UnaryOperatorDefinition {
        public static final UnaryOperatorDefinition PLUS = new Standard("+", Set.of(), OptionalDouble::of, 0);
        public static final UnaryOperatorDefinition MINUS = new Standard("-", Set.of(), x -> OptionalDouble.of(-x), 0);

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Set<String> getAliases() {
            return aliases;
        }

        @Override
        public UnaryOperation getOperation() {
            return operation;
        }

        @Override
        public int getPrecedence() {
            return precedence;
        }
    }
}
