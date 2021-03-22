package app.mvc.panels;

import app.SwingComponent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@SwingComponent
public class PanelContainer extends JPanel implements PanelsUI {
    private final JPanel container = new JPanel();
    private final ScrollPane scrollPane = new ScrollPane();

    private final Map<Panel,ColoredPanel> panels = new HashMap<>();

    private final List<PanelContainerListener> listeners = new CopyOnWriteArrayList<>();

    @Autowired
    private PanelModel panelModel;

    public PanelContainer() {
        super();
    }

    @PostConstruct
    public void setup() {
        setLayout(new BorderLayout());

        container.setLayout(new BoxLayout(container, BoxLayout.LINE_AXIS));
        scrollPane.add(container);

        add(scrollPane, BorderLayout.CENTER);

        panelModel.getPanels().forEach(this::addPanelInternal);
    }

    @Override
    public void reset() {
        container.removeAll();

        scrollPane.revalidate();
        scrollPane.repaint();
    }

    @Override
    public void addPanel(Panel panel) {
        addPanelInternal(panel);

        scrollPane.revalidate();
        scrollPane.repaint();

        listeners.forEach(PanelContainerListener::panelAdded);
    }

    @Override
    public void removePanel(Panel panel) {
        removePanelInternal(panel);

        container.revalidate();
        container.repaint();

        scrollPane.revalidate();
        scrollPane.repaint();

        revalidate();
        repaint();

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
        void panelRemoved();
    }
}
