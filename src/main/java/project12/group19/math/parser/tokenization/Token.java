package project12.group19.math.parser.tokenization;

public record Token<T>(String source, int offset, Kind kind, T value) {
    public enum Kind {
        WHITESPACE,
        COMMA,
        VALUE,
        VARIABLE,
        FUNCTION,
        OPERATOR,
        OPENING_BRACE,
        CLOSING_BRACE
    }
}
