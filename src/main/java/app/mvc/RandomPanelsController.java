package app.mvc;

import app.mvc.model.PanelModel;
import app.mvc.ui.PanelContainer;
import app.mvc.model.PanelModel.PanelListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static app.SpringGuiRunner.runInGui;
import static java.util.Collections.emptyList;

@Service
public class RandomPanelsController implements PanelController {
    private static final Color[] ALLOWED_COLORS = {
            Color.WHITE,
            Color.RED,
            Color.GREEN,
            Color.BLUE,
    };

    @Autowired
    private PanelModel panelModel;

    private final Random random = new Random(System.currentTimeMillis());

    private List<Color> savedColors = emptyList();

    @Autowired
    private PanelContainer panelContainer;

    @PostConstruct
    public void setup() {
        panelModel.addPanelListener(new PanelListener() {
            @Override
            public void panelsAdded(List<Color> colors) {
                runInGui(() -> colors.forEach(panelContainer::addPanel));
            }

            @Override
            public void panelsRemoved(List<Color> colors) {
                //TODO
            }

            @Override
            public void panelsCleared() {
                runInGui(panelContainer::reset);
            }
        });
    }

    @Override
    public void reset() {
        panelModel.clear();
    }

    @Override
    public void load() {
        panelModel.clear();
        panelModel.addPanels(savedColors);
    }

    @Override
    public void save() {
        savedColors = new ArrayList<>(panelModel.getPanels());
    }

    @Override
    public void newPanel() {
        Color color = ALLOWED_COLORS[random.nextInt(ALLOWED_COLORS.length)];
        panelModel.addPanel(color);
    }
}
