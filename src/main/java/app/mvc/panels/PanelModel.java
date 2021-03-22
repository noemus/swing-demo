package app.mvc.panels;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

@Component
public class PanelModel {
    private final List<Panel> panels = new ArrayList<>();

    private final List<PanelListener> listeners = new CopyOnWriteArrayList<>();
    private final AtomicBoolean suspendListeners = new AtomicBoolean(false);

    public List<Panel> getPanels() {
        return unmodifiableList(panels);
    }

    public void addPanel(Panel color) {
        addPanels(singletonList(color));
    }

    public void addPanels(List<Panel> addedColors) {
        panels.addAll(addedColors);
        firePanelsAdded(addedColors);
    }

    public void removePanel(Panel color) {
        removePanels(singletonList(color));
    }

    public void removePanels(List<Panel> removedColors) {
        panels.removeAll(removedColors);
        firePanelsRemoved(removedColors);
    }

    public void clear() {
        panels.clear();
        firePanelsCleared();
    }

    public void addPanelListener(PanelListener listener) {
        listeners.add(0, listener);
    }

    public void removePanelListener(PanelListener listener) {
        listeners.remove(listener);
    }

    private void firePanelsAdded(List<Panel> colors) {
        fireListeners(listener -> listener.panelsAdded(colors));
    }

    private void firePanelsRemoved(List<Panel> colors) {
        fireListeners(listener -> listener.panelsRemoved(colors));
    }

    private void firePanelsCleared() {
        fireListeners(PanelListener::panelsCleared);
    }

    private void fireListeners(Consumer<PanelListener> action) {
        if (suspendListeners.compareAndSet(false, true)) {
            listeners.forEach(action);
            suspendListeners.set(false);
        }
    }

    public interface PanelListener {
        void panelsAdded(List<Panel> colors);
        void panelsRemoved(List<Panel> colors);
        void panelsCleared();
    }
}
