package project12.group19.math.parser.tokenization;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameTokenReader implements TokenReader<String> {
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z][\\w.]*");
    private static final Pattern BRACE_PATTERN = Pattern.compile("\\s*\\(");

    @Override
    public Token<String> read(String input, int offset, List<Token<?>> preceding) {
        Matcher matcher = NAME_PATTERN.matcher(input);

        if (!matcher.find(offset) || matcher.start() != offset) {
            return null;
        }

        String match = matcher.group();
        int continuation = offset + match.length();
        Matcher brace = BRACE_PATTERN.matcher(input);
        Token.Kind kind = brace.find(continuation) && brace.start() == continuation ? Token.Kind.FUNCTION : Token.Kind.VARIABLE;

        return new Token<>(match, offset, kind, match);
    }
}
