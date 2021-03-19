package app.mvc.panels;

import app.mvc.messages.MessageController;
import app.mvc.panels.PanelModel.PanelListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static app.SpringGuiRunner.runInGui;
import static java.util.Collections.emptyList;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;

@Service
public class RandomPanelsController implements PanelController {
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

    private final Random random = new Random(System.currentTimeMillis());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    private PanelModel panelModel;

    @Autowired
    private PanelsUI panelsUI;

    @Autowired
    private MessageController messageController;

    private final PanelListener panelListener = new PanelListener() {
        @Override
        public void panelsAdded(List<Color> colors) {
            runInGui(() -> colors.forEach(panelsUI::addPanel));
        }

        @Override
        public void panelsRemoved(List<Color> colors) {
            throw new UnsupportedOperationException("Not implemented yet!");
        }

        @Override
        public void panelsCleared() {
            runInGui(panelsUI::reset);
        }
    };

    private final AtomicReference<List<Color>> savedColors = new AtomicReference<>(emptyList());

    @PostConstruct
    public void setup() {
        panelModel.addPanelListener(panelListener);
    }

    @PreDestroy
    public void dispose() {
        panelModel.removePanelListener(panelListener);
        executorService.shutdown();
    }

    @Override
    public void reset() {
        if (showConfirmDialog(panelsUI.getComponent(),
                              "Do you like to reset panels?",
                              "Warning",
                              OK_CANCEL_OPTION) == OK_OPTION) {
            panelModel.clear();
            messageController.showMessage("Panels cleared");
        }
    }

    @Override
    public void load() {
        if (showConfirmDialog(panelsUI.getComponent(),
                              "Do you like to load panels?",
                              "Warning",
                              OK_CANCEL_OPTION) == OK_OPTION) {
            executorService.submit(() -> {
                messageController.showMessage("Loading panels...");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                panelModel.clear();
                panelModel.addPanels(savedColors.get());

                messageController.showMessage("Panels loaded");
            });
        }
    }

    @Override
    public void save() {
        executorService.submit(() -> {
            messageController.showMessage("Saving panels...");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            savedColors.set(new ArrayList<>(panelModel.getPanels()));

            messageController.showMessage("Panels saved");
        });

    }

    @Override
    public void newPanel() {
        Color color = ALLOWED_COLORS[random.nextInt(ALLOWED_COLORS.length)];
        panelModel.addPanel(color);
    }
}
