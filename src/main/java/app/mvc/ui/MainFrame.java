package app.mvc.ui;

import app.mvc.ui.PanelContainer.PanelContainerListener;

import app.SpringGuiApp;
import app.SwingComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

@SwingComponent
public class MainFrame extends JFrame implements SpringGuiApp {
    private static final Dimension MINIMUM_SIZE = new Dimension(600, 400);

    public MainFrame(
            @Value("${main.frame.title:Demo Frame}") String title,
            @Value("${main.frame.width:800}") int width,
            @Value("${main.frame.height:600}") int height) {
        super(title);

        setSize(width, height);
        initLayout();
    }

    @Autowired
    private PanelToolbar toolbar;

    @Autowired
    private PanelContainer panelContainer;

    @PostConstruct
    public void setupComponents() {
        panelContainer.addPanelContainerListener(new PanelContainerListener() {
            @Override
            public void panelAdded(PanelContainer panelContainer) {
                panelContainer.scrollToEnd();
            }

            @Override
            public void panelRemoved(PanelContainer panelContainer) {
                // ignore
            }
        });

        add(toolbar, BorderLayout.NORTH);
        add(panelContainer, BorderLayout.CENTER);
    }

    @Override
    public void start() {
        setVisible(true);
    }

    private void initLayout() {
        setLayout(new BorderLayout());
        setMinimumSize(MINIMUM_SIZE);
        setResizable(true);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
