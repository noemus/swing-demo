package app.mvc.panels;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Collections.emptyList;

@Service
public class PanelService {
    private static final Color[] ALLOWED_COLORS = {
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.CYAN,
            Color.YELLOW,
            Color.MAGENTA,
            Color.WHITE,
            Color.BLACK,
            Color.GRAY,
            Color.LIGHT_GRAY,
            Color.DARK_GRAY,
            };

    private final AtomicInteger indexGenerator = new AtomicInteger(0);
    private final Random random = new Random(System.currentTimeMillis());
    private final AtomicReference<List<Panel>> savedPanels = new AtomicReference<>(emptyList());

    private final AtomicBoolean running = new AtomicBoolean(false);

    public Panel createPanel() {
        return new Panel(indexGenerator.getAndIncrement(), randomColor());
    }

    public void save(List<Panel> panels) {
        runServiceAction(() -> doSave(panels));
    }

    public List<Panel> load() {
        return runServiceAction(this::doLoad);
    }

    private boolean doSave(List<Panel> panels) {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

        savedPanels.set(new ArrayList<>(panels));
        return true;
    }

    private List<Panel> doLoad() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return emptyList();
        }

        return new ArrayList<>(savedPanels.get());
    }

    private Color randomColor() {
        return ALLOWED_COLORS[random.nextInt(ALLOWED_COLORS.length)];
    }

    private <T> T runServiceAction(Callable<T> action) {
        if (running.compareAndSet(false, true)) {
            try {
                return action.call();
            } catch (Exception e) {
                throw new IllegalStateException("Service encountered an error: "+e.getMessage()+"!", e);
            } finally {
                running.set(false);
            }
        } else {
            throw new IllegalStateException("Service is busy!");
        }
    }
}
