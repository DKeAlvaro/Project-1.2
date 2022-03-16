package project12.group19.math.parser.tokenization;

import project12.group19.math.parser.OperatorDefinition;
import project12.group19.math.parser.component.ComponentRegistry;

public class OperatorTokenReader implements TokenReader<OperatorDefinition> {
    private final ComponentRegistry registry;

    public OperatorTokenReader(ComponentRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Token<OperatorDefinition> read(String input, int offset) {
        for (String name : registry.getOperatorNames()) {
            if (input.indexOf(name, offset) == offset) {
                return new Token<>(name, offset, Token.Kind.OPERATOR, registry.getOperator(name));
            }
        }

        return null;
    }

    public static TokenReader<OperatorDefinition> standard() {
        return new OperatorTokenReader(ComponentRegistry.standard());
    }
}
