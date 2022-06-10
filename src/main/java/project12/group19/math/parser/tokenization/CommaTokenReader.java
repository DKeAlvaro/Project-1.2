package project12.group19.math.parser.tokenization;

import java.util.List;

public class CommaTokenReader implements TokenReader<String> {
    @Override
    public Token<String> read(String input, int offset, List<Token<?>> preceding) {
        return input.charAt(offset) == ',' ? new Token<>(",", offset, Token.Kind.COMMA, ",") : null;
    }
}
