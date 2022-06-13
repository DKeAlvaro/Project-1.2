package project12.group19.math.parser;

import project12.group19.math.BinaryOperation;

import java.util.Collections;
import java.util.OptionalDouble;
import java.util.Set;

public interface BinaryOperatorDefinition {
    String getName();
    Set<String> getAliases();
    BinaryOperation getOperation();
    int getPrecedence();
    boolean isLeftAssociative();
    default boolean isRightAssociative() {
        return !isLeftAssociative();
    }

    record Standard(
            String name,
            BinaryOperation operation,
            int precedence,
            boolean leftAssociative,
            Set<String> aliases
    ) implements BinaryOperatorDefinition {
        public static final BinaryOperatorDefinition ADD = new Standard("+", (left, right) -> OptionalDouble.of(left + right), 1, true);
        public static final BinaryOperatorDefinition SUBTRACT = new Standard("-", (left, right) -> OptionalDouble.of(left - right), 1, true);
        public static final BinaryOperatorDefinition MULTIPLY = new Standard("*", (left, right) -> OptionalDouble.of(left * right), 2, true);
        public static final BinaryOperatorDefinition DIVIDE = new Standard(
                "/",
                (left, right) -> right == 0 ? OptionalDouble.empty() : OptionalDouble.of(left / right),
                2,
                true
        );
        public static final BinaryOperatorDefinition POWER = new Standard("^", (left, right) -> OptionalDouble.of(Math.pow(left, right)), 3, false);

        public Standard(String name, BinaryOperation operation, int precedence, boolean leftAssociative) {
            this(name, operation, precedence, leftAssociative, Collections.emptySet());
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Set<String> getAliases() {
            return aliases;
        }

        @Override
        public int getPrecedence() {
            return precedence;
        }

        @Override
        public boolean isLeftAssociative() {
            return leftAssociative;
        }

        @Override
        public BinaryOperation getOperation() {
            return operation;
        }
    }
}
