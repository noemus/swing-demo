package app.mvc.panels;

import app.mvc.messages.MessageController;
import app.mvc.panels.PanelModel.PanelListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static app.SpringGuiRunner.runInGui;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;

@Service
public class RandomPanelsController implements PanelController {
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Autowired
    private PanelModel panelModel;

    @Autowired
    private PanelsUI panelsUI;

    @Autowired
    private MessageController messageController;

    @Autowired
    private PanelService panelService;

    private final PanelListener panelListener = new PanelListener() {
        @Override
        public void panelsAdded(List<Panel> panels) {
            runInGui(() -> panels.forEach(panelsUI::addPanel));
        }

        @Override
        public void panelsRemoved(List<Panel> panels) {
            runInGui(() -> panels.forEach(panelsUI::removePanel));
        }

        @Override
        public void panelsCleared() {
            runInGui(panelsUI::reset);
        }
    };

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
            messageController.showWarning("Panels cleared");
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
                    List<Panel> loadedPanels = panelService.load();
                    panelModel.clear();
                    panelModel.addPanels(loadedPanels);
                    messageController.showMessage("Panels loaded");
                } catch (Exception e) {
                    messageController.showError("Panels load failed with: " + e.getMessage());
                }
            });
        }
    }

    @Override
    public void save() {
        executorService.submit(() -> {
            messageController.showMessage("Saving panels...");
            try {
                panelService.save(panelModel.getPanels());
                messageController.showMessage("Panels saved");
            } catch (Exception e) {
                messageController.showError("Panels save failed with: " + e.getMessage());
            }
        });

    }

    @Override
    public void newPanel() {
        panelModel.addPanel(panelService.createPanel());
    }

    @Override
    public void removePanel(Panel panel) {
        panelModel.removePanel(panel);
    }
}
