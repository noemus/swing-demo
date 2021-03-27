package app.mvc;

import app.SpringGuiRunner;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class PanelsDemo {
    public static void main(String[] args) {
        initLogging();
        SpringGuiRunner.create(AppConfig.class).run(MainFrame.class);
    }

    private static void initLogging() {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                           "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$-7s [%3$s] (%2$s) %5$s %6$s%n");

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE);
        consoleHandler.setFormatter(new SimpleFormatter());

        Logger logger = Logger.getLogger("app");
        logger.setLevel(Level.FINE);
        logger.addHandler(consoleHandler);
    }
}
