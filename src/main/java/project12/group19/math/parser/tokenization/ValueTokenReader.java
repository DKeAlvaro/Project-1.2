package project12.group19.math.parser.tokenization;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueTokenReader implements TokenReader<Double> {
    public static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+(?:\\.[0-9]+)?(E[+-][0-9]+)?");

    @Override
    public Token<Double> read(String input, int offset) {
        Matcher matcher = NUMBER_PATTERN.matcher(input);

        if (!matcher.find(offset) || matcher.start() != offset) {
            return null;
        }

        return new Token<>(matcher.group(), offset, Token.Kind.VALUE, Double.parseDouble(matcher.group()));
    }
}
