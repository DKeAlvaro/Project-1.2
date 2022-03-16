package project12.group19.math.parser.tokenization;

public class CommaTokenReader implements TokenReader<String> {
    @Override
    public Token<String> read(String input, int offset) {
        return input.charAt(offset) == ',' ? new Token<>(",", offset, Token.Kind.COMMA, ",") : null;
    }
}
