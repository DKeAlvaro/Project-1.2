package project12.group19.math.parser.tokenization;

import java.util.List;

public interface TokenReader<T> {
    Token<T> read(String input, int offset, List<Token<?>> preceding);
}
