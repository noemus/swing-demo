package app.mvc.panels;

import java.awt.*;

public interface PanelsUI {
    void reset();

    void addPanel(Color color);

    Component getComponent();
}
