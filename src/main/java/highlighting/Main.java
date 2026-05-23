package highlighting;

import highlighting.antlr.*;
import highlighting.color.DarkModeColorResolver;
import highlighting.color.WhiteModeColorResolver;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.Texts;
import highlighting.regex.*;
import highlighting.ui.EditorUI;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

public class Main {

    /*asdfasdf*/
    public static void main(String... args) {
        // Phase I: RegexHighlighter
        SwingUtilities.invokeLater(() -> createEditorUi(
            Texts.START_TEXT,
            new RegexHighlighter(new WhiteModeColorResolver()),
            new RegexHighlighter(new DarkModeColorResolver())
        ));
        int sdf = 's';

        // Phase II: ScanningHighlighter
        SyntaxHighlighter scanning = new ScanningHighlighter();

        // Phase III: AntlrTokenCollector (tokenbasiert)
        SyntaxHighlighter antlrToken = new AntlrTokenCollector();

//    EditorUI.show(Texts.START_TEXT, scanning);
        // EditorUI.show(Texts.START_TEXT, antlrToken);
    }



    @SuppressWarnings("unchecked")
    public static void createEditorUi(String startText, SyntaxHighlighter whiteMode, SyntaxHighlighter darkMode) {
        try {
            Class<EditorUI> c = (Class<EditorUI>) Class.forName("highlighting.ui.EditorUI");
            Constructor<EditorUI> constructor = (Constructor<EditorUI>) Arrays.stream(c.getDeclaredConstructors()).findFirst().get();
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
            cache.put(Color.BLACK, whiteDefault);   // BLACK -> liefert tatsächlich weiß

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
            System.out.println("using fall back option \"normal\" view.");
            EditorUI.show(startText, whiteMode);
        }
    }
}
