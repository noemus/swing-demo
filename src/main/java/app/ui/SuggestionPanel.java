package app.ui;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SuggestionPanel<T> {
    private final JTextComponent textComponent;
//    private final DefaultListModel<AutocompleteValue> listModel = new DefaultListModel<>();
    private final JList<AutocompleteValue> list;
    private final JPopupMenu popupMenu;
    private final AutocompleteFilter<T> autocompleteFilter;

    private int insertionPosition;
    private String subWord;

    public SuggestionPanel(JTextComponent textComponent, AutocompleteFilter<T> autocompleteFilter) {
        this.textComponent = textComponent;
        this.autocompleteFilter = autocompleteFilter;

        popupMenu = new JPopupMenu();
        popupMenu.removeAll();
        popupMenu.setOpaque(false);
        popupMenu.setFocusable(false);

        list = new JList<>();
        list.setBorder(BorderFactory.createEmptyBorder(0, 2, 5, 2));
        list.setFocusable(false);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                final int firstIndex = e.getFirstIndex();
                System.out.println("Selected index: " + firstIndex);
            }
        });

        popupMenu.add(list, BorderLayout.CENTER);
    }

    private Point getLocation(int position) {
        try {
            return textComponent.modelToView(position).getLocation();
        } catch (BadLocationException e) {
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

    private void replace(int position, int length, String newText) throws BadLocationException {
        getDocument().replace(position, length, newText, null);
    }

    public boolean isAtWord(int position) {
        String text = textComponent.getText();
        if (position >= 0 && position <= text.length()) {
            int pos = Math.max(0, position - 1);
            while (pos > 0) {
                if (Character.isLetter(text.charAt(pos))) {
                    pos--;
                } else if (!Character.isWhitespace(text.charAt(pos))) {
                    return false;
                } else if (pos == text.length() - 1) {
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

    private boolean isBeforeWord(int position, String text) {
        if (position == text.length()) {
            return true;
        }
        if (position < text.length()) {
            return Character.isLetter(text.charAt(position));
        }
        return false;
    }

    private String getSubWord(int position, String text) {
        if (position < 0 || position > text.length()) {
            return null;
        }

        int start = position;
        if (position == text.length()) {
            start--;
        }

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
        return text.substring(start, position + 1);
    }

    private int endOfWord(int position, String text) {
        if (position < 0 || position > text.length()) {
            return -1;
        }

        int end = position;
        while (end < text.length()) {
            if (Character.isWhitespace(text.charAt(end))) {
                break;
            }
            end++;
        }
        return end;
    }

    private int startOfWord(int position, String text) {
        if (position < 0 || position > text.length()) {
            return -1;
        }

        int start = Math.max(0, position - 1);
        while (start > 0) {
            if (Character.isWhitespace(text.charAt(start))) {
                start++;
                break;
            }
            start--;
        }
        if (start > position) {
            return position;
        }
        return start;
    }

    private void showSuggestions(int position, String text) {
        int end = endOfWord(position, text);
        int start = startOfWord(position, text);

        if (start == -1 || end == -1) {
            return;
        }

        insertionPosition = start;
        subWord = text.substring(start, end);

        AutocompleteValue[] listData = autocompleteFilter.matchedList(subWord);

        if (listData.length == 1) {
            SwingUtilities.invokeLater(() -> insertSuggestion(listData[0]));
        } else if (listData.length > 0) {
            list.setListData(listData);
            list.setSelectedIndex(-1);
            popupMenu.pack();

            if (!isActive()) {
                showPopup();
            }
        } else {
            hideSuggestion();
        }
    }

    private void showPopup() {
        Point location = getLocation(this.insertionPosition);
        if (location == null) {
            return;
        }

        int baseline = Math.max(0, textComponent.getBaseline(0, 0));
        popupMenu.show(textComponent, location.x, baseline + location.y);

        initSuggestionKeyListener();

        SwingUtilities.invokeLater(textComponent::requestFocusInWindow);
    }

    public boolean insertSelection() {
        final AutocompleteValue selectedValue = list.getSelectedValue();
        if (selectedValue != null) {
            insertSuggestion(selectedValue);
            return true;
        }
        return false;
    }

    private void insertSuggestion(AutocompleteValue selectedValue) {
        String selectedSuggestion = selectedValue.getText();
        try {
            disableDocumentListener = true;
            replace(insertionPosition, subWord.length(), selectedSuggestion);
        } catch (BadLocationException e) {
            // ignore
        } finally {
            disableDocumentListener = false;
        }
        hideSuggestion();
    }

    public void moveUp() {
        int index = Math.max(list.getSelectedIndex() - 1, 0);
        System.out.println("move up: index=" + index);
        selectIndex(index);
        System.out.println("move up: index=" + list.getSelectedIndex() + " - finished");
    }

    public void moveDown() {
        int index = Math.min(list.getSelectedIndex() + 1, list.getModel().getSize() - 1);
        System.out.println("move down: index=" + index);
        selectIndex(index);
        System.out.println("move down: index=" + list.getSelectedIndex() + " - finished");
        System.out.println("move down: selected=" + list.getSelectedValue());
    }

    private void selectIndex(int index) {
        final int position = textComponent.getCaretPosition();
        list.setSelectedIndex(index);
        SwingUtilities.invokeLater(() -> textComponent.setCaretPosition(position));
    }

    public void open(int position) {
        showSuggestions(position, textComponent.getText());
    }

    private void hideSuggestion() {
        restoreListeners();
        popupMenu.setVisible(false);
    }

    private boolean disableDocumentListener = false;
    private final DocumentListener autocompleteDocumentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            if (!isActive() || disableDocumentListener) {
                return;
            }

            int pos = e.getOffset();

            if (isAtWord(pos)) {
                showSuggestions(pos, textComponent.getText());
            } else {
                hideSuggestion();
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (!isActive() || disableDocumentListener) {
                return;
            }

            int pos = e.getOffset();
            String text = new StringBuilder(textComponent.getText())
                    .delete(pos, pos + e.getLength())
                    .toString();

            if (isBeforeWord(pos, text)) {
                showSuggestions(pos, text);
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
//            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
//                if (insertSelection()) {
//                    e.consume();
//                    int position = textComponent.getCaretPosition();
//                    SwingUtilities.invokeLater(() -> {
//                        try {
//                            textComponent.getDocument().remove(position - 1, 1);
//                        } catch (BadLocationException e1) {}
//                    });
//                }
//            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (!isActive()) {
                return;
            }

//            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
//                moveDown();
//                e.consume();
//            } else if (e.getKeyCode() == KeyEvent.VK_UP) {
//                moveUp();
//                e.consume();
//            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                moveDown();
                e.consume();
            } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                moveUp();
                e.consume();
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (insertSelection()) {
                    e.consume();
                    int position = textComponent.getCaretPosition();
                    SwingUtilities.invokeLater(() -> {
//                        try {
//                            int position = textComponent.getCaretPosition();
//                            textComponent.getDocument().remove(position - 1, 1);
//                        } catch (BadLocationException e1) {}
                    });
                }
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                hideSuggestion();
                e.consume();
            }
        }
    };
    private CaretListener autocompleteCaretListener = new CaretListener() {
        @Override
        public void caretUpdate(CaretEvent e) {
            if (!isActive()) {
                return;
            }

            if (!isAtWord(e.getDot())) {
                hideSuggestion();
            }
        }
    };

    private DocumentListener[] documentListeners;
    private CaretListener[] caretListeners;
    private KeyListener[] keyListeners;

    private void saveListeners() {
        final AbstractDocument document = getDocument();

//        documentListeners = document.getListeners(DocumentListener.class);
//        if (documentListeners != null) {
//            Arrays.stream(documentListeners).forEach(document::removeDocumentListener);
//        }
//        caretListeners = textComponent.getCaretListeners();
//        if (caretListeners != null) {
//            Arrays.stream(caretListeners).forEach(textComponent::removeCaretListener);
//        }
//        keyListeners = textComponent.getKeyListeners();
//        if (keyListeners != null) {
//            Arrays.stream(keyListeners).forEach(textComponent::removeKeyListener);
//        }
    }

    private void initSuggestionKeyListener() {
        saveListeners();

        getDocument().addDocumentListener(autocompleteDocumentListener);
        System.out.println("Register key listener");
        textComponent.addKeyListener(autocompleteKeyListener);
//        textComponent.addCaretListener(autocompleteCaretListener);
    }

    private void restoreListeners() {
        final AbstractDocument document = getDocument();

        document.removeDocumentListener(autocompleteDocumentListener);
//        if (documentListeners != null) {
//            Arrays.stream(documentListeners).forEach(document::addDocumentListener);
//        }
//        documentListeners = null;

//        textComponent.removeCaretListener(autocompleteCaretListener);
//        if (caretListeners != null) {
//            Arrays.stream(caretListeners).forEach(textComponent::addCaretListener);
//        }
//        caretListeners = null;

        System.out.println("Un-Register key listener");
        textComponent.removeKeyListener(autocompleteKeyListener);
//        if (keyListeners != null) {
//            Arrays.stream(keyListeners).forEach(textComponent::addKeyListener);
//        }
//        keyListeners = null;
    }

    public boolean isActive() {
        return popupMenu.isVisible();
    }
}
