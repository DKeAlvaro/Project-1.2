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
                new OperatorTokenReader(registry),
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
                case OPERATOR -> {
                    OperatorDefinition definition = (OperatorDefinition) token.value();

                    while (hasCandidateOperator(operators, definition)) {
                        Token<?> previous = operators.pop();
                        components.add(new Operator(previous.source(), ((OperatorDefinition) previous.value())));
                    }

                    operators.add(token);
                }
                case OPENING_BRACE -> operators.push(token);
                case CLOSING_BRACE -> {
                    while (!operators.isEmpty() && operators.peek().kind() != Token.Kind.OPENING_BRACE) {
                        Token<?> operator = operators.pop();
                        components.add(new Operator(operator.source(), (OperatorDefinition) operator.value()));
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

            if (operator.kind() == Token.Kind.OPERATOR) {
                components.add(new Operator(operator.source(), (OperatorDefinition) operator.value()));
            }
        }

        return new InfixExpression(source, components);
    }

    private static boolean hasCandidateOperator(Stack<Token<?>> stack, OperatorDefinition current) {
        if (stack.isEmpty()) {
            return false;
        }

        Token<?> tip = stack.peek();

        if (tip.kind() == Token.Kind.OPENING_BRACE) {
            return false;
        }

        if (tip.kind() != Token.Kind.OPERATOR) {
            return true;
        }

        OperatorDefinition comparison = ((OperatorDefinition) tip.value());

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
