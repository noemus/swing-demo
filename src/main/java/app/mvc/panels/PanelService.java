package app.mvc.panels;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    public Panel createPanel() {
        return new Panel(indexGenerator.getAndIncrement(), randomColor());
    }

    public void save(List<Panel> panels) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        savedPanels.set(new ArrayList<>(panels));
    }

    public List<Panel> load() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return emptyList();
        }

        return new ArrayList<>(savedPanels.get());
    }

    private Color randomColor() {
        return ALLOWED_COLORS[random.nextInt(ALLOWED_COLORS.length)];
    }
}
