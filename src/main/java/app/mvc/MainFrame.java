package app.mvc;

import app.SpringGuiApp;
import app.SwingComponent;
import app.mvc.messages.MessagePanel;
import app.mvc.panels.PanelContainer;
import app.mvc.panels.PanelContainer.PanelContainerListener;
import app.mvc.panels.PanelToolbar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

@SwingComponent
public class MainFrame extends JFrame implements SpringGuiApp {
    private static final Dimension MINIMUM_SIZE = new Dimension(400, 240);

    public MainFrame(
            @Value("${main.frame.title:Panels Demo}") String title,
            @Value("${main.frame.width:800}") int width,
            @Value("${main.frame.height:240}") int height) {
        super(title);

        setSize(width, height);
        initLayout();
    }

    @Autowired
    private PanelToolbar toolbar;

    @Autowired
    private MessagePanel messagePanel;

    @Autowired
    private PanelContainer panelContainer;

    @PostConstruct
    public void setupComponents() {
        panelContainer.addPanelContainerListener(new PanelContainerListener() {
            @Override
            public void panelAdded() {
                panelContainer.scrollToEnd();
            }

            @Override
            public void panelRemoved() {
                // ignore
            }
        });

        add(toolbar, BorderLayout.NORTH);
        add(panelContainer, BorderLayout.CENTER);
        add(messagePanel, BorderLayout.SOUTH);
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
