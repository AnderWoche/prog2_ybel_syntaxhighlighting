---
title: Student Support Code for 'Syntaxhighlighting' Task
---

<!-- pandoc -s -f markdown -t markdown --columns=94 --reference-links=true README.md -->

## About

This represents the student support code for the [Syntaxhighlighting task].

## License

This [work] by [Carsten Gips] and [contributors] is licensed under [MIT].

  [Syntaxhighlighting task]: https://github.com/Programmiermethoden-CampusMinden/Prog2-Lecture/tree/master/homework
  [work]: https://github.com/Programmiermethoden-CampusMinden/prog2_ybel_syntaxhighlighting
  [Carsten Gips]: https://github.com/cagix
  [contributors]: https://github.com/Programmiermethoden-CampusMinden/prog2_ybel_syntaxhighlighting/graphs/contributors
  [MIT]: LICENSE.md


### Vergleichen Sie das so erzeugte Syntaxhighlighting mit den Varianten von Blatt 04. Wo gibt es Unterschiede, was sind die Gründe dafür? Welche der Varianten ist aufwändiger in der Implementierung?
Regex kann kontext lesen lexer ist einfach kontextlose tokens. also kann man mit regex mehr darstellen.

# Pretty-Printer — Beobachtungen und Diskussion

> Frage: *Was beobachten Sie bei der Ausgabe Ihres Pretty-Printers? Wieso fehlen bestimmte Teile aus dem Input?*

## Was im Output fehlt — und warum

### 1. Alle Kommentare verschwinden

**Beobachtung:** Egal ob `//`, `/* … */` oder `/** … */` — im Output sind sie weg.

**Ursache:** In der Grammatik `MiniJava.g4` sind Kommentare auf den **HIDDEN-Channel** geroutet (`-> channel(HIDDEN)`). Der `CommonTokenStream`, den der Parser nutzt, gibt den Parser-Regeln nur Tokens vom Default-Channel — Hidden-Tokens tauchen also gar nicht im Parse-Tree auf. Der Visitor *sieht* sie nicht, also kann er sie auch nicht ausgeben.

### 2. Alle originalen Leerzeilen / Whitespaces verschwinden

**Beobachtung:** Im Input sind logische Gruppen durch Leerzeilen getrennt (z. B. zwischen Feldgruppen, zwischen Methoden). Im Output sind diese Trennungen weg — alles dicht aneinander.

**Ursache:** Whitespace ist in der Grammatik mit `-> skip` markiert — der Lexer wirft die Tokens komplett weg, sie kommen nicht mal in den Token-Stream. Vertikale Struktur entsteht nur durch die `nl()`-Aufrufe im Visitor.

### 3. Bei syntaktisch korrektem Input

Werden weggelassen:

- alle Originalkommentare
- alle Original-Leerzeilen
- alle inkonsistenten Einrückungen (jetzt einheitlich nach `indentWidth`)
- redundanter Whitespace innerhalb von Zeilen

**Was bleibt:** der semantische Inhalt des Programms (Schlüsselwörter, Identifier, Literale, Operatoren, Strukturzeichen). Der Pretty-Printer reproduziert das Programm **inhaltlich verlustfrei**, aber **stilistisch normalisiert**.

## Einfacher Code vs. komplexer Code

### Einfacher Code (MiniJava-konform)

Beispiel: kleine Klasse mit Feld + Methode + if/while

- Saubere, konsistente Formatierung
- Alle Strukturen werden korrekt eingerückt
- Output ist syntaktisch wieder validierbar mit derselben Grammatik
- Einschränkung: Kommentare fehlen, Originalspacing in Ausdrücken (z. B. `x=1+2`) ist eng

### Komplexer Code (echtes Java)

Beispiel: `EditorUI.java`, `LibgdxSetup` oder ähnliches

Viele Java-Features sind in MiniJava **nicht definiert**:

- Lambdas (`->`)
- Generics (`<T>`)
- `static`
- `try`/`catch`
- `for`
- `switch`
- anonyme Klassen
- Cast-Ausdrücke
- multi-catch
- `import static`
- Annotationsargumente mit Arrays

Der Parser geht in **Error-Recovery**: Tokens werden teilweise konsumiert, Knoten unvollständig oder leer im Tree. Das führt zu typischen Symptomen:

- **Leere Method-Bodies** (Inhalt wurde als unparsbar verworfen)
- **Mysteriöse Leerzeilen** mitten in Klassen (mehrere Error-Recovery-Versuche → mehrere leere `classBodyDeclaration`-Knoten → mehrere `nl()`)
- **`->` zerfällt zu `-` `>`** (Lambda-Pfeil ist kein Token)
- **Token-Reste am Ende** (z. B. `private private @Override public …`), weil der Parser nach dem Fehler nicht mehr in den Tree zurückfindet und Tokens als Error-Nodes durchrutschen
- **Cast-Ausdrücke verschwinden ganz**, weil sie nicht in `expression` passen

### Bei bewusst kaputtem Input

Beispiel: das `@Over-ride 'someText'` im Vorgabe-Text `Texts.java`

- Parser versucht mehrfach, eine Mitgliedsdeklaration zu bauen, scheitert, generiert dabei mehrere partielle/leere Knoten
- Jeder dieser Knoten löst in `visitClassBody` ein `nl()` aus → **mehrere ungewollte Leerzeilen** im Output

## Kernaussage

Der Pretty-Printer kann nur ausgeben, was im Parse-Tree steht. Hidden-Channel-Tokens (Kommentare) und Skip-Tokens (Whitespace) gehen verloren, weil sie gar nicht erst im Tree erscheinen.

Bei syntaktisch korrektem MiniJava-Input ist das Ergebnis sauber normalisiert. Bei Code, der die MiniJava-Grammatik überschreitet, produziert die Error-Recovery des Parsers Lücken und überschüssige Strukturknoten — daher fehlende Inhalte und überflüssige Leerzeilen.

## Mögliche Verbesserungen

- **Kommentare erhalten:** Statt `-> channel(HIDDEN)` die Kommentare über den Default-Channel laufen lassen UND im Visitor explizit behandeln — oder über `TokenStreamRewriter` / direkten Zugriff auf den `HIDDEN`-Channel die Kommentare nachträglich einfügen.
- **Original-Leerzeilen erhalten:** Im Token-Stream nach `\n\n` zwischen Tokens schauen (geht aber nur, wenn Whitespace nicht geskipt, sondern auch hidden wäre).
- **Robustheit bei Syntaxfehlern:** `BailErrorStrategy` setzen — bei Fehlern komplett abbrechen, statt unzuverlässig zu raten. Oder in `visitClassBody` leere/error-haltige Knoten überspringen.
