package highlighting.color;

import java.awt.*;

public class WhiteModeColorResolver implements ColorResolver {

    @Override
    public Color resolveColor(ColorType colorType) {
        return switch (colorType) {
            case STRING_LITERAL_COLOUR -> MiniJavaColours.STRING_LITERAL_COLOUR;
            case CHAR_LITERAL_COLOUR -> MiniJavaColours.CHAR_LITERAL_COLOUR;
            case KEYWORD_COLOUR -> MiniJavaColours.KEYWORD_COLOUR;
            case ANNOTATION_COLOUR -> MiniJavaColours.ANNOTATION_COLOUR;
            case LINE_COMMENT_COLOUR -> MiniJavaColours.LINE_COMMENT_COLOUR;
            case BLOCK_COMMENT_COLOUR -> MiniJavaColours.BLOCK_COMMENT_COLOUR;
            case JAVADOC_COMMENT_COLOUR -> MiniJavaColours.JAVADOC_COMMENT_COLOUR;
            case IDENTIFIER_COLOUR -> MiniJavaColours.IDENTIFIER_COLOUR;
            case NUMBER_COLOUR -> MiniJavaColours.NUMBER_COLOUR;
            case OPERATOR_COLOUR -> MiniJavaColours.OPERATOR_COLOUR;
            case CLASS_NAME_COLOUR -> MiniJavaColours.CLASS_NAME_COLOUR;
            case METHOD_NAME_COLOUR -> MiniJavaColours.METHOD_NAME_COLOUR;
            case METHOD_CALL_COLOUR -> MiniJavaColours.METHOD_CALL_COLOUR;
            case CONSTRUCTOR_CALL_COLOUR -> MiniJavaColours.CONSTRUCTOR_CALL_COLOUR;
            case CONSTANT_COLOUR -> MiniJavaColours.CONSTANT_COLOUR;
            case PARAMETER_COLOUR -> MiniJavaColours.PARAMETER_COLOUR;
        };
    }
}
