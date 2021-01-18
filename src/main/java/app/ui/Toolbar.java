package app.ui;

import app.SwingComponent;
import app.api.TextUpdater;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;

@SwingComponent
public class Toolbar extends JPanel {
    @Autowired
    private TextUpdater textUpdater;

    public Toolbar() {
        setLayout(new FlowLayout());

        button("Hello").addActionListener(event -> textUpdater.addText("Hello"));
        button("Goodbye").addActionListener(event -> textUpdater.addText("Goodbye"));
        button("Clear").addActionListener(event -> textUpdater.clear());
    }

    private JButton button(String name) {
        JButton btn = new JButton(name);
        btn.setName(name);
        add(btn);
        return btn;
    }
}
