package app.ui;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

public class SuggestionPanel<T> {
    private final JTextComponent textComponent;
    private final JList<TextPane.AutocompleteValue> list;
    private final JPopupMenu popupMenu;
    private final TextPane.AutocompleteFilter<T> autocompleteFilter;

    private int insertionPosition;
    private String subWord;

    public SuggestionPanel(JTextComponent textComponent, TextPane.AutocompleteFilter<T> autocompleteFilter) {
        this.textComponent = textComponent;
        this.autocompleteFilter = autocompleteFilter;
        this.list = new JList<>();

        popupMenu = new JPopupMenu();
        popupMenu.removeAll();
        popupMenu.setOpaque(false);
        popupMenu.setBorder(null);
        popupMenu.add(list, BorderLayout.CENTER);
    }

    private Point getLocation(int position) {
        try {
            return textComponent.modelToView(position).getLocation();
        } catch (BadLocationException e) {
            e.printStackTrace();
            return null;
        }
    }

    AbstractDocument getDocument() {
        Document document = textComponent.getDocument();
        if (document instanceof AbstractDocument) {
            return (AbstractDocument) document;
        }
        throw new IllegalStateException("Document should extend " + AbstractDocument.class.getName());
    }

    void replace(int position, int length, String newText) throws BadLocationException {
        getDocument().replace(position, length, newText, null);
    }

    boolean isAtWord(int position) {
        String text = textComponent.getText();
        if (position >= 0 && position <= text.length()) {
            int pos = Math.max(0, position - 1);
            while (pos > 0) {
                if (Character.isLetter(text.charAt(pos))) {
                    pos--;
                } else if (!Character.isWhitespace(text.charAt(pos))) {
                    return false;
                } else {
                    pos++;
                    break;
                }
            }
            return Character.isLetter(text.charAt(pos));
        }
        return false;
    }

    boolean isBeforeWord(int position) {
        if (position == 0) {
            return true;
        }
        String text = textComponent.getText();
        if (position == text.length()) {
            return true;
        }
        if (position < text.length()) {
            return Character.isLetter(text.charAt(position));
        }
        return false;
    }

    public String getSubWord(int position) {
        String text = textComponent.getText();
        if (position < 0 || position > text.length()) {
            return null;
        }

        int start = Math.max(0, position - 1);
        while (start > 0) {
            if (Character.isLetter(text.charAt(start))) {
                start--;
            } else if (!Character.isWhitespace(text.charAt(start))) {
                return null;
            } else {
                start++;
                break;
            }
        }
        if (start > position) {
            return null;
        }
        return text.substring(start, position);
    }

    private void show(int position, String subWord, Point location) {
        this.insertionPosition = position;
        this.subWord = subWord;

        list.setListData(autocompleteFilter.matchedList(subWord));

        popupMenu.show(textComponent, location.x, textComponent.getBaseline(0, 0) + location.y);

        initSuggestionKeyListener();

        SwingUtilities.invokeLater(textComponent::requestFocusInWindow);
    }

    public boolean insertSelection() {
        if (list.getSelectedValue() != null) {
            try {
                String selectedSuggestion = list.getSelectedValue().getText();
                replace(insertionPosition, subWord.length(), selectedSuggestion);
                return true;
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
            hideSuggestion();
        }
        return false;
    }

    public void moveUp() {
        int index = Math.min(list.getSelectedIndex() - 1, 0);
        selectIndex(index);
    }

    public void moveDown() {
        int index = Math.min(list.getSelectedIndex() + 1, list.getModel().getSize() - 1);
        selectIndex(index);
    }

    private void selectIndex(int index) {
        final int position = textComponent.getCaretPosition();
        list.setSelectedIndex(index);
        SwingUtilities.invokeLater(() -> textComponent.setCaretPosition(position));
    }

    private void showSuggestion() {
        hideSuggestion();

        int position = textComponent.getCaretPosition();

        String subWord = getSubWord(position);
        if (subWord == null) {
            return;
        }

        Point location = getLocation(position - subWord.length());
        if (location == null) {
            return;
        }

        show(position, subWord, location);
    }

    private void hideSuggestion() {
        restoreListeners();
        popupMenu.setVisible(false);
    }

    protected void initSuggestionKeyListener() {
        saveListeners();

        getDocument().addDocumentListener(autocompleDocumentListener);
        textComponent.addKeyListener(autocompleteKeyListener);
        textComponent.addCaretListener(autocompleteCaretListener);
    }

    private final DocumentListener autocompleDocumentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            int pos = e.getOffset();

            if (isAtWord(pos)) {
                SwingUtilities.invokeLater(SuggestionPanel.this::showSuggestion);
            } else {
                hideSuggestion();
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            int pos = e.getOffset();
            if (isActive() && isBeforeWord(pos)) {
                SwingUtilities.invokeLater(SuggestionPanel.this::showSuggestion);
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {

        }
    };
    private final KeyListener autocompleteKeyListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
            if (!isActive()) {
                return;
            }
            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                if (insertSelection()) {
                    e.consume();
                    int position = textComponent.getCaretPosition();
                    SwingUtilities.invokeLater(() -> {
                        try {
                            textComponent.getDocument().remove(position - 1, 1);
                        } catch (BadLocationException e1) {}
                    });
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (!isActive()) {
                return;
            }

            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                moveDown();
            } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                moveUp();
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {

        }
    };
    private CaretListener autocompleteCaretListener = new CaretListener() {
        @Override
        public void caretUpdate(CaretEvent e) {
            if (!isAtWord(e.getDot())) {
                hideSuggestion();
            }
        }
    };

    private DocumentListener[] documentListeners;
    private CaretListener[] caretListeners;
    private KeyListener[] keyListeners;

    private void saveListeners() {
        documentListeners = getDocument().getListeners(DocumentListener.class);
        caretListeners = textComponent.getCaretListeners();
        keyListeners = textComponent.getKeyListeners();
    }

    private void restoreListeners() {
        final AbstractDocument document = getDocument();

        document.removeDocumentListener(autocompleDocumentListener);
        if (documentListeners != null) {
            Arrays.stream(documentListeners).forEach(document::addDocumentListener);
        }
        textComponent.removeCaretListener(autocompleteCaretListener);
        if (caretListeners != null) {
            Arrays.stream(caretListeners).forEach(textComponent::addCaretListener);
        }
        textComponent.removeKeyListener(autocompleteKeyListener);
        if (keyListeners != null) {
            Arrays.stream(keyListeners).forEach(textComponent::addKeyListener);
        }
    }

    boolean isActive() {
        return popupMenu.isVisible();
    }
}
