package app.mvc.panels;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

@Component
public class PanelModel {
    private final List<Color> colors = new ArrayList<>();

    private final List<PanelListener> listeners = new CopyOnWriteArrayList<>();
    private final AtomicBoolean suspendListeners = new AtomicBoolean(false);

    public List<Color> getPanels() {
        return unmodifiableList(colors);
    }

    public void addPanel(Color color) {
        addPanels(singletonList(color));
    }

    public void addPanels(List<Color> addedColors) {
        colors.addAll(addedColors);
        firePanelsAdded(addedColors);
    }

    public void removePanel(Color color) {
        removePanels(singletonList(color));
    }

    public void removePanels(List<Color> removedColors) {
        colors.removeAll(removedColors);
        firePanelsRemoved(removedColors);
    }

    public void clear() {
        colors.clear();
        firePanelsCleared();
    }

    public void addPanelListener(PanelListener listener) {
        listeners.add(0, listener);
    }

    public void removePanelListener(PanelListener listener) {
        listeners.remove(listener);
    }

    private void firePanelsAdded(List<Color> colors) {
        fireListeners(listener -> listener.panelsAdded(colors));
    }

    private void firePanelsRemoved(List<Color> colors) {
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
        void panelsAdded(List<Color> colors);
        void panelsRemoved(List<Color> colors);
        void panelsCleared();
    }
}
