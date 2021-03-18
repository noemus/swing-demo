package app.suggestions.ui;

interface AutocompleteValue {
    String toString();

    String getText();

    boolean matches(String subWord);
}
