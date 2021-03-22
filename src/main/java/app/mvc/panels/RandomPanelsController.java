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
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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

                panelModel.clear();
                panelModel.addPanels(panelService.load());

                messageController.showMessage("Panels loaded");
            });
        }
    }

    @Override
    public void save() {
        executorService.submit(() -> {
            messageController.showMessage("Saving panels...");

            panelService.save(panelModel.getPanels());

            messageController.showMessage("Panels saved");
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
