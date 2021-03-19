package app.mvc.panels;

import app.SwingComponent;
import app.mvc.panels.PanelController;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;

@SwingComponent
public class PanelToolbar extends JPanel {
    @Autowired
    private PanelController panelController;

    public PanelToolbar() {
        setLayout(new FlowLayout());

        button("Add").addActionListener(event -> panelController.newPanel());
        button("Save").addActionListener(event -> panelController.save());
        button("Load").addActionListener(event -> panelController.load());
        button("Reset").addActionListener(event -> panelController.reset());
    }

    private JButton button(String name) {
        JButton btn = new JButton(name);
        btn.setName(name);
        btn.setMaximumSize(btn.getPreferredSize());
        add(btn);
        return btn;
    }
}
