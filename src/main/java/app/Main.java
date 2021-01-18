package app;

import app.ui.MainFrame;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
                MainFrame frame = context.getBean(MainFrame.class);
                SwingUtilities.invokeLater(frame::init);
            }
        });
    }
}
