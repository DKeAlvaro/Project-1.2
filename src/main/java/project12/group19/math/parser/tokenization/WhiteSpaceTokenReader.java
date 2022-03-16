package project12.group19.math.parser.tokenization;

public class WhiteSpaceTokenReader implements TokenReader<String> {
    @Override
    public Token<String> read(String input, int offset) {
        int length = 0;
        while (input.length() > offset + length && Character.isWhitespace(input.charAt(offset + length))) {
            length++;
        }

        String source = input.substring(offset, offset + length);
        return length == 0 ? null : new Token<>(source, offset, Token.Kind.WHITESPACE, source);
    }
}
