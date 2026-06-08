package highlighting.color;

import highlighting.antlr.MiniJavaLexer;

public enum ColorType {
    STRING_LITERAL_COLOUR,
    CHAR_LITERAL_COLOUR,
    KEYWORD_COLOUR,
    ANNOTATION_COLOUR,
    LINE_COMMENT_COLOUR,
    BLOCK_COMMENT_COLOUR,
    JAVADOC_COMMENT_COLOUR,
    IDENTIFIER_COLOUR,
    NUMBER_COLOUR,
    OPERATOR_COLOUR,
    CLASS_NAME_COLOUR,
    METHOD_NAME_COLOUR,
    METHOD_CALL_COLOUR,
    CONSTRUCTOR_CALL_COLOUR,
    CONSTANT_COLOUR,
    PARAMETER_COLOUR;

    /**
     * Mappt einen MiniJavaLexer-Tokentyp auf den passenden {@link ColorType}.
     *
     * @param tokenType Konstante aus {@link MiniJavaLexer} (z. B. {@code MiniJavaLexer.PACKAGE})
     * @return passender {@code ColorType} oder {@code null}, wenn das Token nicht eingefärbt wird
     */
    public static ColorType colorFor(int tokenType) {
        switch (tokenType) {
            case MiniJavaLexer.PACKAGE:
            case MiniJavaLexer.IMPORT:
            case MiniJavaLexer.CLASS:
            case MiniJavaLexer.PUBLIC:
            case MiniJavaLexer.PRIVATE:
            case MiniJavaLexer.FINAL:
            case MiniJavaLexer.RETURN:
            case MiniJavaLexer.NULL:
            case MiniJavaLexer.NEW:
            case MiniJavaLexer.IF:
            case MiniJavaLexer.ELSE:
            case MiniJavaLexer.WHILE:
            case MiniJavaLexer.EXTENDS:
            case MiniJavaLexer.IMPLEMENTS:
                return KEYWORD_COLOUR;

            case MiniJavaLexer.STRING_LITERAL:
                return STRING_LITERAL_COLOUR;

            case MiniJavaLexer.CHAR_LITERAL:
                return CHAR_LITERAL_COLOUR;

            case MiniJavaLexer.LINE_COMMENT:
                return LINE_COMMENT_COLOUR;

            case MiniJavaLexer.BLOCK_COMMENT:
                return BLOCK_COMMENT_COLOUR;

            case MiniJavaLexer.JAVADOC_COMMENT:
                return JAVADOC_COMMENT_COLOUR;

            case MiniJavaLexer.IDENTIFIER:
                return IDENTIFIER_COLOUR;

            case MiniJavaLexer.AT:
                return ANNOTATION_COLOUR;

            case MiniJavaLexer.PLUS:
            case MiniJavaLexer.MINUS:
            case MiniJavaLexer.STAR:
            case MiniJavaLexer.SLASH:
            case MiniJavaLexer.PERCENT:
            case MiniJavaLexer.ASSIGN:
            case MiniJavaLexer.LT:
            case MiniJavaLexer.GT:
            case MiniJavaLexer.BANG:
            case MiniJavaLexer.QUESTION:
            case MiniJavaLexer.COLON:
            case MiniJavaLexer.LE:
            case MiniJavaLexer.GE:
            case MiniJavaLexer.EQUAL:
            case MiniJavaLexer.NOTEQUAL:
            case MiniJavaLexer.AND:
            case MiniJavaLexer.OR:
                return OPERATOR_COLOUR;

            default:
                return null;
        }
    }
}
