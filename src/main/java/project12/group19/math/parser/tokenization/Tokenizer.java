package project12.group19.math.parser.tokenization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tokenizer {
    private final List<TokenReader<?>> readers;

    public Tokenizer(List<TokenReader<?>> readers) {
        this.readers = readers;
    }

    public List<Token<?>> tokenize(String expression) {
        int offset = 0;
        List<Token<?>> accumulator = new ArrayList<>(expression.length() / 2);

        while (offset < expression.length()) {
            Token<?> token = getToken(expression, offset, Collections.unmodifiableList(accumulator));

            if (token == null) {
                throw new IllegalArgumentException("Failed to parse expression: unknown token at " + offset);
            }

            accumulator.add(token);
            offset += token.source().length();
        }

        return accumulator;
    }

    private Token<?> getToken(String expression, int offset, List<Token<?>> preceding) {
        for (TokenReader<?> reader : readers) {
            Token<?> token = reader.read(expression, offset, preceding);
            if (token != null) {
                return token;
            }
        }

        return null;
    }
}
