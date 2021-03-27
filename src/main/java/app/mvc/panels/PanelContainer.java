package app.mvc.panels;

import app.SwingComponent;
import app.mvc.SGLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

@SwingComponent
public class PanelContainer extends JPanel implements PanelsUI {
    private static final Logger LOGGER = Logger.getLogger(PanelContainer.class.getSimpleName());

    private final JPanel lhsPanel = new JPanel(new BorderLayout());
    private final JPanel rhsPanel = new JPanel(new BorderLayout());

    private final JPanel container = new JPanel();
    private final ScrollPane scrollPane = new ScrollPane();

    private final Map<Panel,ColoredPanel> panels = new HashMap<>();

    private final List<PanelContainerListener> listeners = new CopyOnWriteArrayList<>();

    @Autowired
    private PanelModel panelModel;

    public PanelContainer() {
        super();
        LOGGER.fine("<init>");
    }

    @PostConstruct
    public void setup() {
        LOGGER.fine("setup()");
        SGLayout layout = new SGLayout(1, 3, SGLayout.CENTER, SGLayout.TOP, 1, 0);
        layout.setAlignment(0, 1, SGLayout.FILL, SGLayout.TOP);
        layout.setMargins(2, 2, 2, 2);
        layout.setColumnScale(0, .2);
        layout.setColumnScale(2, .2);
        setLayout(layout);

        container.setLayout(new BoxLayout(container, BoxLayout.LINE_AXIS));
        scrollPane.add(container);
        scrollPane.setPreferredSize(new Dimension(500, 120));

        Box lhsBox = Box.createVerticalBox();
        JButton submit = new JButton("Submit");
        submit.setMaximumSize(submit.getPreferredSize());
        lhsBox.add(submit);
        lhsBox.add(Box.createVerticalStrut(3));
        JButton clear = new JButton("Clear");
        clear.setMaximumSize(clear.getPreferredSize());
        lhsBox.add(clear);
        lhsPanel.add(lhsBox, BorderLayout.CENTER);
        add(lhsPanel);

        add(scrollPane);

        JComboBox<String> comboBox = new JComboBox<>(new String[]{"Option 1", "Option 2"});
        comboBox.setMaximumSize(comboBox.getPreferredSize());
        rhsPanel.add(comboBox, BorderLayout.CENTER);
        add(rhsPanel);

        panelModel.getPanels().forEach(this::addPanelInternal);
    }

    @Override
    public void reset() {
        container.removeAll();

        container.revalidate();
        container.repaint();
    }

    @Override
    public void addPanel(Panel panel) {
        addPanelInternal(panel);

        container.revalidate();
        container.repaint();

        listeners.forEach(PanelContainerListener::panelAdded);
    }

    @Override
    public void removePanel(Panel panel) {
        removePanelInternal(panel);

        container.revalidate();
        container.repaint();

        listeners.forEach(PanelContainerListener::panelRemoved);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    public void addPanelContainerListener(PanelContainerListener listener) {
        listeners.add(0, listener);
    }

    public void removePanelContainerListener(PanelContainerListener listener) {
        listeners.remove(listener);
    }

    public void scrollToEnd() {
        int maximum = scrollPane.getHAdjustable().getMaximum();
        scrollPane.setScrollPosition(maximum, scrollPane.getVAdjustable().getValue());
    }

    private void addPanelInternal(Panel panel) {
        ColoredPanel coloredPanel = new ColoredPanel(panel.color(), panel.label());
        panels.put(panel, coloredPanel);
        container.add(coloredPanel);

        coloredPanel.addPanelClickedListener(e -> panelModel.removePanel(panel));
    }

    private void removePanelInternal(Panel panel) {
        ColoredPanel coloredPanel = panels.remove(panel);
        container.remove(coloredPanel);
    }

    public interface PanelContainerListener {
        void panelAdded();
        default void panelRemoved() {}
    }
}
