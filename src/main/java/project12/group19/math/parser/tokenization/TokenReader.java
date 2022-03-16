package project12.group19.math.parser.tokenization;

public interface TokenReader<T> {
    Token<T> read(String input, int offset);
}
