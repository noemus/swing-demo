package app.suggestions.ui;

import app.SwingComponent;
import app.suggestions.api.TextUpdater;

import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

@SwingComponent
public class TextPane extends JPanel implements TextUpdater {
    private final JTextArea textArea;
    private final SuggestionPanel<CriteriaType> suggestion;

    private final DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            if (suggestion.isActive()) {
                return;
            }
            final int caretPosition = e.getOffset();
            if (suggestion.isAtWord(caretPosition)) {
                System.out.println("Open suggestions: " + textArea.getText());
                suggestion.open(caretPosition);
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            //updateModel();
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
        suggestion = new SuggestionPanel<>(textArea, new AutocompleteFilter<>(CriteriaType.values(), CriteriaTypeValue::new));

        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    private void caretMoved() {
        // do nothing
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
    }

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
            return criteriaType.name().toLowerCase().contains(subWord.toLowerCase());
        }
    }
}
