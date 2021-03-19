package app.mvc.panels;

import app.SwingComponent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SwingComponent
public class PanelContainer extends JPanel implements PanelsUI {
    private final JPanel panel = new JPanel();
    private final ScrollPane scrollPane = new ScrollPane();

    private final List<PanelContainerListener> listeners = new CopyOnWriteArrayList<>();

    @Autowired
    private PanelModel panelModel;

    public PanelContainer() {
        super();
    }

    @PostConstruct
    public void setup() {
        setLayout(new BorderLayout());

        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        scrollPane.add(panel);

        add(scrollPane, BorderLayout.CENTER);

        panelModel.getPanels().forEach(this::addPanelInternal);
    }

    @Override
    public void reset() {
        panel.removeAll();

        scrollPane.revalidate();
        scrollPane.repaint();
    }

    @Override
    public void addPanel(Color color) {
        addPanelInternal(color);

        scrollPane.revalidate();
        scrollPane.repaint();

        listeners.forEach(PanelContainerListener::panelAdded);
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

    private void addPanelInternal(Color color) {
        panel.add(new ColoredPanel(color));
    }

    public interface PanelContainerListener {
        void panelAdded();
        void panelRemoved();
    }
}
