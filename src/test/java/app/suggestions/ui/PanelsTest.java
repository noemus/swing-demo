package app.suggestions.ui;

import app.SpringGuiRunner;
import app.mvc.AppConfig;
import app.mvc.MainFrame;

public class PanelsTest {
    public static void main(String[] args) {
        SpringGuiRunner.create(AppConfig.class).runAsTest(MainFrame.class);
    }
}
