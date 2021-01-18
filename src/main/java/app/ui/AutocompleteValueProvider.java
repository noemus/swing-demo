package app.ui;

@FunctionalInterface
interface AutocompleteValueProvider<T> {
    AutocompleteValue toAutocompleValue(T item);
}
