package project12.group19.math.parser.tokenization;

public class BraceTokenReader implements TokenReader<String> {
    @Override
    public Token<String> read(String input, int offset) {
        return switch (input.charAt(offset)) {
            case '(' -> new Token<>("(", offset, Token.Kind.OPENING_BRACE, "(");
            case ')' -> new Token<>(")", offset, Token.Kind.CLOSING_BRACE, ")");
            default -> null;
        };
    }
}
