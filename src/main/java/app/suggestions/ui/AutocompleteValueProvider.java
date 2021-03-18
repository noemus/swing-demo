package app.suggestions.ui;

@FunctionalInterface
interface AutocompleteValueProvider<T> {
    AutocompleteValue toAutocompleValue(T item);
}
