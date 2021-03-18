package app.suggestions.ui;

import java.util.stream.Stream;

public class AutocompleteFilter<T> {
    private final AutocompleteValue[] choices;

    public AutocompleteFilter(T[] choices, AutocompleteValueProvider<T> valueProvider) {
        this.choices = Stream.of(choices)
                             .map(valueProvider::toAutocompleValue)
                             .toArray(AutocompleteValue[]::new);
    }

    public AutocompleteValue[] matchedList(String subWord) {
        System.out.println("Matches: " + subWord);
        AutocompleteValue[] values = Stream.of(choices)
                                           .filter(choice -> choice.matches(subWord))
                                           .toArray(AutocompleteValue[]::new);
        System.out.println("Count: " + values.length);
        return values;
    }
}
