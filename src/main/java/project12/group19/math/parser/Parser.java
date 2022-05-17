package project12.group19.math.parser;

import project12.group19.math.parser.component.ComponentRegistry;
import project12.group19.math.parser.expression.InfixExpression;
import project12.group19.math.parser.expression.component.*;
import project12.group19.math.parser.tokenization.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Parser {
    private final ComponentRegistry registry;
    private final Tokenizer tokenizer;

    public Parser(ComponentRegistry registry) {
        this.registry = registry;
        tokenizer = new Tokenizer(Arrays.asList(
                new WhiteSpaceTokenReader(),
                new BraceTokenReader(),
                new CommaTokenReader(),
                new UnaryOperatorTokenReader(registry),
                new BinaryOperatorTokenReader(registry),
                new ValueTokenReader(),
                new NameTokenReader()
        ));
    }

    public InfixExpression parse(String source) {
        List<Token<?>> tokens = tokenizer.tokenize(source);
        List<Component> components = new ArrayList<>(tokens.size());
        Stack<Token<?>> operators = new Stack<>();

        for (Token<?> token : tokens) {
            switch (token.kind()) {
                case VALUE -> components.add(new Value(token.source(), (Double) token.value()));
                case VARIABLE -> components.add(new Variable(token.source()));
                case FUNCTION -> operators.add(token);
                case BINARY_OPERATOR -> {
                    BinaryOperatorDefinition definition = (BinaryOperatorDefinition) token.value();

                    while (hasCandidateOperator(operators, definition)) {
                        components.add(operatorToComponent(operators.pop()));
                    }

                    operators.add(token);
                }
                case UNARY_OPERATOR, OPENING_BRACE -> operators.push(token);
                case CLOSING_BRACE -> {
                    while (!operators.isEmpty() && operators.peek().kind() != Token.Kind.OPENING_BRACE) {
                        components.add(operatorToComponent(operators.pop()));
                    }

                    if (operators.isEmpty()) {
                        throw new IllegalArgumentException("Mismatched braces");
                    }

                    operators.pop();

                    if (!operators.isEmpty() && operators.peek().kind() == Token.Kind.FUNCTION) {
                        components.add(getFunction(operators.pop().source()));
                    }
                }
            }
        }

        while (!operators.isEmpty()) {
            Token<?> operator = operators.pop();

            if (operator.kind() == Token.Kind.FUNCTION) {
                components.add(getFunction(operator.source()));
            }

            if (operator.kind() == Token.Kind.BINARY_OPERATOR) {
                components.add(new BinaryOperator(operator.source(), (BinaryOperatorDefinition) operator.value()));
            }

            if (operator.kind() == Token.Kind.UNARY_OPERATOR) {
                components.add(new UnaryOperator(operator.source(), (UnaryOperatorDefinition) operator.value()));
            }
        }

        return new InfixExpression(source, components);
    }

    private static Component operatorToComponent(Token<?> operator) {
        if (operator.value() instanceof BinaryOperatorDefinition) {
            return new BinaryOperator(operator.source(), (BinaryOperatorDefinition) operator.value());
        }

        return new UnaryOperator(operator.source(), (UnaryOperatorDefinition) operator.value());
    }

    private static boolean hasCandidateOperator(Stack<Token<?>> stack, BinaryOperatorDefinition current) {
        if (stack.isEmpty()) {
            return false;
        }

        Token<?> tip = stack.peek();

        if (tip.kind() == Token.Kind.OPENING_BRACE) {
            return false;
        }

        if (tip.kind() != Token.Kind.BINARY_OPERATOR) {
            return true;
        }

        BinaryOperatorDefinition comparison = ((BinaryOperatorDefinition) tip.value());

        if (comparison.getPrecedence() > current.getPrecedence()) {
            return true;
        }

        if (comparison.getPrecedence() < current.getPrecedence()) {
            return false;
        }

        return current.isLeftAssociative();
    }

    private Component getFunction(String operation) {
        OperationDefinition definition = registry.getOperation(operation);
        if (definition == null) {
            throw new IllegalArgumentException("Unregistered operation " + operation);
        }
        return new Operation(operation, definition);
    }
}
