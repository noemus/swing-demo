package app.ui;

import app.SwingComponent;
import app.api.TextUpdater;

import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.stream.Stream;

@SwingComponent
public class TextPane extends JPanel implements TextUpdater {
    private final JTextArea textArea;

    private final DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateModel();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {

        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            // do nothing
        }
    };

    private final CaretListener caretListener = e -> caretMoved();


    public TextPane() {
        setLayout(new BorderLayout());
        textArea = new JTextArea();

        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    private void caretMoved() {

    }

    private void updateModel() {

    }

    @Override
    public void addText(String text) {
        textArea.append(text + System.lineSeparator());
    }

    @Override
    public void clear() {
        textArea.setText("");
    }

    public void init() {
        textArea.getDocument().addDocumentListener(documentListener);
        textArea.addCaretListener(caretListener);

        suggestion = new SuggestionPanel<>(textArea, new AutocompleteFilter<>(CriteriaType.values(), CriteriaTypeValue::new));
    }

    private SuggestionPanel<CriteriaType> suggestion;

    enum CriteriaType {
        AuditDate,
        AuditUser,
        AuditWhatever,

        User,
        ValueDate,
        Currency,
        CurrencyPair,

        Amount,
        DownstreamUser,
        TradeId,
        TradeDate,
        MaturityDate,

        Rate,
        FixingRate,
        FixingDate,
        Book,
    }

    static class AutocompleteFilter<T> {
        private final AutocompleteValue[] choices;

        AutocompleteFilter(T[] choices, AutocompleteValueProvider<T> valueProvider) {
            this.choices = Stream.of(choices)
                                 .map(valueProvider::toAutocompleValue)
                                 .toArray(AutocompleteValue[]::new);
        }

        public AutocompleteValue[] matchedList(String subWord) {
            return Stream.of(choices)
                         .filter(choice -> choice.matches(subWord))
                         .toArray(AutocompleteValue[]::new);
        }
    }

    @FunctionalInterface
    interface AutocompleteValueProvider<T> {
        AutocompleteValue toAutocompleValue(T item);
    }

    interface AutocompleteValue {
        String toString();
        String getText();
        boolean matches(String subWord);
    }

    static class CriteriaTypeValue implements AutocompleteValue {
        private final CriteriaType criteriaType;

        CriteriaTypeValue(CriteriaType criteriaType) {
            this.criteriaType = criteriaType;
        }

        @Override
        public String toString() {
            return criteriaType.name();
        }

        @Override
        public String getText() {
            return "'" + criteriaType.name() + "'";
        }

        @Override
        public boolean matches(String subWord) {
            return criteriaType.name().toLowerCase().contains(subWord);
        }
    }
}
