package app.mvc.panels;

import java.awt.*;

public interface PanelsUI {
    void reset();

    void addPanel(Panel panel);
    void removePanel(Panel panel);

    Component getComponent();
}
