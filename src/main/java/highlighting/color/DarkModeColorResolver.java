package highlighting.color;

import java.awt.*;

public class DarkModeColorResolver implements ColorResolver {

    @Override
    public Color resolveColor(ColorType colorType) {
        return switch (colorType) {
            case STRING_LITERAL_COLOUR -> MiniJavaDarkColours.STRING_LITERAL_COLOUR;
            case CHAR_LITERAL_COLOUR -> MiniJavaDarkColours.CHAR_LITERAL_COLOUR;
            case KEYWORD_COLOUR -> MiniJavaDarkColours.KEYWORD_COLOUR;
            case ANNOTATION_COLOUR -> MiniJavaDarkColours.ANNOTATION_COLOUR;
            case LINE_COMMENT_COLOUR -> MiniJavaDarkColours.LINE_COMMENT_COLOUR;
            case BLOCK_COMMENT_COLOUR -> MiniJavaDarkColours.BLOCK_COMMENT_COLOUR;
            case JAVADOC_COMMENT_COLOUR -> MiniJavaDarkColours.JAVADOC_COMMENT_COLOUR;
            case IDENTIFIER_COLOUR -> MiniJavaDarkColours.IDENTIFIER_COLOUR;
            case NUMBER_COLOUR -> MiniJavaDarkColours.NUMBER_COLOUR;
            case OPERATOR_COLOUR -> MiniJavaDarkColours.OPERATOR_COLOUR;
            case CLASS_NAME_COLOUR -> MiniJavaDarkColours.CLASS_NAME_COLOUR;
            case METHOD_NAME_COLOUR -> MiniJavaDarkColours.METHOD_NAME_COLOUR;
            case METHOD_CALL_COLOUR -> MiniJavaDarkColours.METHOD_CALL_COLOUR;
            case CONSTRUCTOR_CALL_COLOUR -> MiniJavaDarkColours.CONSTRUCTOR_CALL_COLOUR;
            case CONSTANT_COLOUR -> MiniJavaDarkColours.CONSTANT_COLOUR;
            case PARAMETER_COLOUR -> MiniJavaDarkColours.PARAMETER_COLOUR;
        };
    }
}
