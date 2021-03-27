package app.mvc.messages;

import app.SwingComponent;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

import static javax.swing.BorderFactory.createCompoundBorder;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.BorderFactory.createMatteBorder;

@SwingComponent
public class MessagePanel extends JPanel implements MessageUI {
    private static final Logger LOGGER = Logger.getLogger(MessagePanel.class.getSimpleName());

    private final JLabel messageLabel = new JLabel();

    public MessagePanel() {
        LOGGER.fine("<init>");
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(createCompoundBorder(
                createEmptyBorder(2, 2, 2, 2),
                createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY))
        );

        messageLabel.setVisible(false);
        messageLabel.setOpaque(true);

        add(messageLabel, BorderLayout.CENTER);
    }

    @Override
    public void showMessage(Message message) {
        switch (message.type()) {
            case ERROR -> messageLabel.setForeground(Color.RED);
            case WARNING -> messageLabel.setForeground(Color.ORANGE);
            case INFO -> messageLabel.setForeground(Color.DARK_GRAY);
            default -> throw new IllegalStateException("Unexpected value: " + message.type());
        }
        messageLabel.setText(message.text());
        messageLabel.setVisible(true);
    }

    @Override
    public void clearMessage() {
        messageLabel.setText(null);
        messageLabel.setVisible(false);
    }
}
