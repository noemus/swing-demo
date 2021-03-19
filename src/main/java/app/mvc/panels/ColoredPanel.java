package app.mvc.panels;

import javax.swing.*;
import java.awt.*;

public class ColoredPanel extends JPanel {
    private static final Dimension PANEL_SIZE = new Dimension(100, 100);

    public ColoredPanel(Color color) {
        super(new BorderLayout());
        setPreferredSize(PANEL_SIZE);
        setMinimumSize(PANEL_SIZE);
        setMaximumSize(PANEL_SIZE);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(3,3,3,3),
                BorderFactory.createLineBorder(Color.BLACK)
        ));

        JPanel comp = new JPanel();
        comp.setOpaque(true);
        comp.setBackground(color);
        add(comp, BorderLayout.CENTER);
    }
}
