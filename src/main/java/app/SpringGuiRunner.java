package app;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;

import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.SwingUtilities.isEventDispatchThread;

public class SpringGuiRunner {

    private final Class<?>[] configClasses;

    public SpringGuiRunner(Class<?>... configClasses) {
        this.configClasses = configClasses;
    }

    public static SpringGuiRunner create(Class<?>... configClasses) {
        return new SpringGuiRunner(configClasses);
    }

    public static void runInGui(Runnable action) {
        if (isEventDispatchThread()) {
            action.run();
        } else {
            invokeLater(action);
        }
    }

    public <T extends SpringGuiApp> void run(Class<T> appClass) {
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(configClasses);
        context.registerShutdownHook();
        SwingUtilities.invokeLater(() -> context.getBean(appClass).start());
    }

    public <T extends SpringGuiApp> void runAsTest(Class<T> appClass) {
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(configClasses);
        SwingUtilities.invokeLater(() -> context.getBean(appClass).start());
    }
}
