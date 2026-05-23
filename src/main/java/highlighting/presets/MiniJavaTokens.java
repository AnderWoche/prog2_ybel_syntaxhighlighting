package highlighting.presets;

import highlighting.color.ColorType;
import highlighting.regex.Token;
import java.util.List;
import java.util.regex.Pattern;

public final class MiniJavaTokens {

    public static List<Token> defaultTokens() {
        return List.of(
                Token.of(
                        Pattern.compile("/\\*\\*.*?\\*/", Pattern.DOTALL),
                        ColorType.JAVADOC_COMMENT_COLOUR),
                Token.of(
                        Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL),
                        ColorType.BLOCK_COMMENT_COLOUR),
                Token.of(Pattern.compile("//[^\\n]*"), ColorType.LINE_COMMENT_COLOUR),
                Token.of(
                        Pattern.compile("\"([^\"\\\\]|\\\\.)*\""), ColorType.STRING_LITERAL_COLOUR),
                Token.of(Pattern.compile("'([^'\\\\]|\\\\.)'"), ColorType.CHAR_LITERAL_COLOUR),
                Token.of(Pattern.compile("@\\w+"), ColorType.ANNOTATION_COLOUR),
                Token.of(
                        Pattern.compile(
                                "\\b(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue"
                                    + "|default|do|double|else|enum|extends|final|finally|float|for|goto|if"
                                    + "|implements|import|instanceof|int|interface|long|native|new|package"
                                    + "|private|protected|public|return|short|static|strictfp|super|switch"
                                    + "|synchronized|this|throw|throws|transient|try|void|volatile|while"
                                    + "|true|false|null|var|record|sealed|yield)\\b"),
                        ColorType.KEYWORD_COLOUR),
                Token.of(Pattern.compile("\\b\\d+(\\.\\d+)?\\b"), ColorType.NUMBER_COLOUR),
                Token.of(
                        Pattern.compile(
                                "\\b(?:public|private|protected)(?:\\s+(?:static|final|abstract|synchronized))*\\s+(?:void|[A-Z]\\w*)\\s+(\\w+)\\s*\\("),
                        1, // ← Gruppe 1 = der Methodenname
                        ColorType.METHOD_NAME_COLOUR),
                Token.of(Pattern.compile("(?<=\\.)\\w+(?=\\s*\\()"), ColorType.METHOD_CALL_COLOUR),
                Token.of(
                        Pattern.compile("\\bnew\\s+([A-Z]\\w*)(?=\\s*[<(])"),
                        1,
                        ColorType.CONSTRUCTOR_CALL_COLOUR),
                Token.of(
                        Pattern.compile("\\b[A-Z][a-zA-Z0-9_]*[a-z][a-zA-Z0-9_]*\\b"),
                        ColorType.CLASS_NAME_COLOUR),
                Token.of(Pattern.compile("\\b[A-Z][A-Z0-9_]*\\b"), ColorType.CONSTANT_COLOUR),
                Token.of(
                        Pattern.compile("(?<=[(,])\\s*\\w+\\s+(\\w+)(?=\\s*[,)])"),
                        1, // ← nur den Namen
                        ColorType.PARAMETER_COLOUR),
                Token.of(
                        Pattern.compile("\\b[A-Za-z_][A-Za-z0-9_]*\\b"),
                        ColorType.IDENTIFIER_COLOUR),
                Token.of(Pattern.compile("[-+*/=<>!&|%^~?:]+"), ColorType.OPERATOR_COLOUR));
    }
}
