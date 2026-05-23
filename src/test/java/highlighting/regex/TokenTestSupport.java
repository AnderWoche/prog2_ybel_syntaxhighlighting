package highlighting.regex;

import highlighting.color.ColorResolver;
import highlighting.color.ColorType;
import highlighting.color.WhiteModeColorResolver;
import highlighting.core.HighlightRegion;
import highlighting.presets.MiniJavaTokens;
import java.util.List;

/**
 * Shared helpers for token tests. Looks up the production {@link Token} by its {@link ColorType},
 * so tests are decoupled from the order in {@link MiniJavaTokens#defaultTokens()}.
 */
public final class TokenTestSupport {

    /** Tests don't care about the actual colour — any resolver is fine. */
    public static final ColorResolver RESOLVER = new WhiteModeColorResolver();

    private TokenTestSupport() {}

    /** Find the production token for a given colour type. */
    public static Token tokenFor(ColorType type) {
        return MiniJavaTokens.defaultTokens().stream()
                .filter(t -> t.colorType() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No token defined for " + type));
    }

    /** Convenience: apply the token for {@code type} to {@code text} and return all regions. */
    public static List<HighlightRegion> match(ColorType type, String text) {
        return tokenFor(type).test(text, RESOLVER);
    }

    /** Convenience: extract the matched substring for a region. */
    public static String slice(String text, HighlightRegion r) {
        return text.substring(r.start(), r.end());
    }
}
