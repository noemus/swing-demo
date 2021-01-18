package app.ui;

interface AutocompleteValue {
    String toString();

    String getText();

    boolean matches(String subWord);
}
