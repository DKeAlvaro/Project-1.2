package project12.group19.math.parser.tokenization;

import project12.group19.math.parser.BinaryOperatorDefinition;
import project12.group19.math.parser.component.ComponentRegistry;

import java.util.List;

public class BinaryOperatorTokenReader implements TokenReader<BinaryOperatorDefinition> {
    private final ComponentRegistry registry;

    public BinaryOperatorTokenReader(ComponentRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Token<BinaryOperatorDefinition> read(String input, int offset, List<Token<?>> preceding) {
        for (String name : registry.getBinaryOperatorNames()) {
            if (input.indexOf(name, offset) == offset) {
                return new Token<>(name, offset, Token.Kind.BINARY_OPERATOR, registry.getBinaryOperator(name));
            }
        }

        return null;
    }

    public static TokenReader<BinaryOperatorDefinition> standard() {
        return new BinaryOperatorTokenReader(ComponentRegistry.standard());
    }
}
