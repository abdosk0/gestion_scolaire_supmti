package utils;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.regex.Pattern;

public final class InputValidators {

    // Lettres (accents compris), espaces, tiret et apostrophe – pour noms/prénoms
    private static final Pattern NOM       = Pattern.compile("[\\p{L} '-]*");
    // Chiffres, +, espaces, tiret et parenthèses – pour téléphone
    private static final Pattern TELEPHONE = Pattern.compile("[0-9+ ()-]*");
    // Nombre décimal positif avec au plus un séparateur (. ou ,)
    private static final Pattern DECIMAL   = Pattern.compile("\\d*[.,]?\\d*");
    // Validation e-mail (à la soumission)
    private static final Pattern EMAIL     = Pattern.compile("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    private InputValidators() {}

    /** N'autorise que des lettres, espaces, tirets et apostrophes. */
    public static void lettresSeulement(TextField field) {
        appliquer(field, NOM);
    }

    /** N'autorise que des chiffres et symboles de téléphone. */
    public static void telephone(TextField field) {
        appliquer(field, TELEPHONE);
    }

    /** N'autorise qu'un nombre décimal positif. */
    public static void decimal(TextField field) {
        appliquer(field, DECIMAL);
    }

    /** Vrai si la chaîne est un e-mail valide. */
    public static boolean estEmailValide(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }

    private static void appliquer(TextField field, Pattern motif) {
        field.setTextFormatter(new TextFormatter<>(change ->
                motif.matcher(change.getControlNewText()).matches() ? change : null));
    }
}
