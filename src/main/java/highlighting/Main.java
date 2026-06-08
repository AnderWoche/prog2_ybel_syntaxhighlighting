package highlighting;

import highlighting.antlr.*;
import highlighting.color.DarkModeColorResolver;
import highlighting.color.WhiteModeColorResolver;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.Texts;
import highlighting.regex.*;
import highlighting.ui.EditorUI;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Main {

    public static void main(String... args) {

        MiniJavaLexer lexer = new MiniJavaLexer(CharStreams.fromString(Texts.START_TEXT));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniJavaParser parser = new MiniJavaParser(tokens);
        MiniJavaParser.CompilationUnitContext tree = parser.compilationUnit();

        Scanner sc = new Scanner(System.in);
        System.out.print("Leerzeichen pro Einrückstufe (z.B. 2, 4, 8): ");
        int indent = sc.nextInt();
        sc.close();

        PrettyPrinterVisitor pp = new PrettyPrinterVisitor(indent);
        pp.visit(tree);
        System.out.println(pp.result());
        System.exit(0);


        // Phase I: RegexHighlighter
//        SwingUtilities.invokeLater(
//                () ->
//                        createEditorUi(
//                                Texts.START_TEXT,
//                                new RegexHighlighter(new WhiteModeColorResolver()),
//                                new RegexHighlighter(new DarkModeColorResolver())));

        // Phase II: ScanningHighlighter
        SyntaxHighlighter scanning = new ScanningHighlighter();

        // Phase III: AntlrTokenCollector (tokenbasiert)
        SwingUtilities.invokeLater(
            () ->
                createEditorUi(
                    Texts.START_TEXT,
                    new AntlrTokenCollector(new WhiteModeColorResolver()),
                    new AntlrTokenCollector(new DarkModeColorResolver())));

    }

    /**
     * Setzt per Reflection die Hintergrundfarbe des Editors auf Dark Mode.
     *
     * <p>Da die Klasse {@link EditorUI} offiziell nicht verändert werden darf, erfolgt die
     * Anpassung auf indirektem Weg. Diese Lösung habe ich als persönliche Herausforderung
     * umgesetzt.
     *
     * @param startText der initial anzuzeigende Text
     * @param whiteMode Highlighter für den hellen Modus (Fallback)
     * @param darkMode Highlighter für den dunklen Modus
     */
    @SuppressWarnings("unchecked")
    public static void createEditorUi(
            String startText, SyntaxHighlighter whiteMode, SyntaxHighlighter darkMode) {
        try {
            Class<EditorUI> c = (Class<EditorUI>) Class.forName("highlighting.ui.EditorUI");
            Constructor<EditorUI> constructor =
                    (Constructor<EditorUI>)
                            Arrays.stream(c.getDeclaredConstructors()).findFirst().get();
            constructor.setAccessible(true);
            EditorUI editor = constructor.newInstance(startText, darkMode);
            Field field = c.getDeclaredField("editorPane");
            field.setAccessible(true);
            JTextPane pane = (JTextPane) field.get(editor);
            pane.setBackground(Color.decode("#414141"));
            pane.setForeground(Color.WHITE);
            pane.setCaretColor(Color.WHITE);
            pane.setSelectionColor(Color.decode("#214283"));
            pane.setSelectedTextColor(Color.WHITE);

            StyledDocument doc = pane.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, Color.WHITE);
            doc.setCharacterAttributes(0, doc.getLength(), attrs, false);

            Field cacheField = c.getDeclaredField("styleCache");
            cacheField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Color, Style> cache = (Map<Color, Style>) cacheField.get(editor);

            Style whiteDefault = pane.addStyle("dark-default", null);
            StyleConstants.setForeground(whiteDefault, Color.WHITE);
            cache.put(Color.BLACK, whiteDefault); // BLACK -> liefert tatsächlich weiß

        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchFieldException e) {
            e.printStackTrace();
            System.out.println("using fall back option \"normal\" view.");
            EditorUI.show(startText, whiteMode);
        }
    }
}
