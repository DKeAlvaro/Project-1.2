package project12.group19.math.parser.tokenization;

import project12.group19.math.parser.UnaryOperatorDefinition;
import project12.group19.math.parser.component.ComponentRegistry;

import java.util.List;
import java.util.Set;

public class UnaryOperatorTokenReader implements TokenReader<UnaryOperatorDefinition> {
    private static final Set<Token.Kind> RESTRICTED_PRECEDING_TOKEN_KINDS = Set.of(
            Token.Kind.VARIABLE,
            Token.Kind.VALUE,
            Token.Kind.CLOSING_BRACE
    );
    private final ComponentRegistry registry;

    public UnaryOperatorTokenReader(ComponentRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Token<UnaryOperatorDefinition> read(String input, int offset, List<Token<?>> preceding) {
        Token.Kind last = last(preceding);

        if (last == null || RESTRICTED_PRECEDING_TOKEN_KINDS.contains(last)) {
            // then it's a binary operator
            return null;
        }

        for (String name : registry.getUnaryOperatorNames()) {
            if (input.indexOf(name, offset) == offset) {
                UnaryOperatorDefinition operator = registry.getUnaryOperator(name);
                return new Token<>(name, offset, Token.Kind.UNARY_OPERATOR, operator);
            }
        }

        return null;
    }

    private static Token.Kind last(List<Token<?>> preceding) {
        for (int i = preceding.size() - 1; i >= 0; i--) {
            Token<?> token = preceding.get(i);

            if (Token.Kind.WHITESPACE.equals(token.kind())) {
                continue;
            }

            return token.kind();
        }

        return null;
    }
}
