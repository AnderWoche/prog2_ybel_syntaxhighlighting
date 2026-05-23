package highlighting.regex;

import highlighting.color.ColorResolver;
import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import java.util.List;
import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents a lexical token defined by a regular expression.
 *
 * <p>The class encapsulates a precompiled regular expression ({@link Pattern}) to be applied to
 * input text in the {@link Token#test(String, ColorResolver)} method. Each token has an associated
 * colour used to highlight the matched text. You can also specify the capturing group to be used
 * for highlighting – this is usually 0 (i.e. the entire match will be highlighted), but can be set
 * to a different value when the regular expression contains groups and only a specific group should
 * be highlighted.
 */
public final class Token {
    private final Pattern pattern;
    private final int matchingGroup;
    private final ColorType colorType;

    private Token(Pattern pattern, int matchingGroup, ColorType colorType) {
        this.pattern = pattern;
        this.matchingGroup = matchingGroup;
        this.colorType = colorType;
    }

    /**
     * Creates a new token.
     *
     * @param pattern the precompiled regular expression applied to the text in {@link
     *     Token#test(String, ColorResolver)}
     * @param matchingGroup the capturing group to be used for highlighting
     * @param colour the colour used for highlighting for this token
     * @return a new {@code Token} instance
     */
    public static Token of(Pattern pattern, int matchingGroup, ColorType colour) {
        return new Token(pattern, matchingGroup, colour);
    }

    /**
     * Creates a new token that uses the entire match for highlighting.
     *
     * @param pattern the precompiled regular expression applied to the text in {@link
     *     Token#test(String, ColorResolver)}
     * @param colour the colour used for highlighting for this token
     * @return a new {@code Token} instance
     */
    public static Token of(Pattern pattern, ColorType colour) {
        return new Token(pattern, 0, colour);
    }

    /**
     * Applies this token (its regular expression) to the given input string.
     *
     * @param s the input string to which this pattern is applied
     * @return a list of all matches found using the pattern
     */
    public List<HighlightRegion> test(String s, ColorResolver colorResolver) {
        return pattern.matcher(s)
                .results()
                .map(mr -> toRegion(mr, colorResolver))
                .collect(Collectors.toList());
    }

    /**
     * Converts a match result into a {@link HighlightRegion} using the configured matching group
     * and colour.
     */
    private HighlightRegion toRegion(MatchResult mr, ColorResolver colorResolver) {
        return new HighlightRegion(
                mr.start(matchingGroup),
                mr.end(matchingGroup),
                colorResolver.resolveColor(colorType));
    }

    public Pattern pattern() {
        return pattern;
    }

    public int matchingGroup() {
        return matchingGroup;
    }

    public ColorType colorType() {
        return colorType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Token) obj;
        return Objects.equals(this.pattern, that.pattern)
                && this.matchingGroup == that.matchingGroup
                && Objects.equals(this.colorType, that.colorType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, matchingGroup, colorType);
    }
}
