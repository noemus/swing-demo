package app.suggestions.ui;

import app.SwingComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

@SwingComponent
public class MainFrame extends JFrame {
    private static final Dimension MINIMUM_SIZE = new Dimension(800, 600);

    public MainFrame(
            @Value("${main.frame.title:Demo Frame}") String title,
            @Value("${main.frame.width:800}") int width,
            @Value("${main.frame.height:600}") int height) {
        super(title);

        setSize(width, height);
        initLayout();
    }

    @Autowired
    private Toolbar toolbar;

    @Autowired
    private TextPane textPane;

    @PostConstruct
    public void setupComponents() {
        add(toolbar, BorderLayout.NORTH);
        add(textPane, BorderLayout.CENTER);
    }

    public void init() {
        setVisible(true);
        textPane.init();
    }

    private void initLayout() {
        setLayout(new BorderLayout());
        setMinimumSize(MINIMUM_SIZE);
        setResizable(true);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
