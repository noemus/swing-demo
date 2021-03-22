package app.mvc.panels;

public interface PanelController {
    void reset();
    void load();
    void save();
    void newPanel();
    void removePanel(Panel panel);
}
