package app.mvc;

import app.SpringGuiRunner;
import app.mvc.ui.MainFrame;

public class PanelsDemo {
    public static void main(String[] args) {
        SpringGuiRunner.create(AppConfig.class).run(MainFrame.class);
    }
}
