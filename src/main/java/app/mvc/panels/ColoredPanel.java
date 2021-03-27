package app.mvc.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class ColoredPanel extends JPanel {

    private static final Dimension PANEL_SIZE = new Dimension(74, 74);

    private final JPanel panel = new JPanel();

    public ColoredPanel(Color color, String label) {
        super(new BorderLayout());

        setPreferredSize(PANEL_SIZE);
        setMinimumSize(PANEL_SIZE);
        setMaximumSize(PANEL_SIZE);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(3,3,3,3),
                BorderFactory.createLineBorder(Color.BLACK)
        ));

        panel.setOpaque(true);
        panel.setBackground(color);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.setToolTipText("Click to remove panel");

        JLabel panelLabel = new JLabel(label);
        panelLabel.setFont(new Font(panelLabel.getFont().getName(), Font.BOLD, 40));
        panel.add(panelLabel, BorderLayout.CENTER);

        add(panel, BorderLayout.CENTER);
    }

    public void addPanelClickedListener(Consumer<MouseEvent> clickHandler) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                clickHandler.accept(e);
            }
        });
    }
}
