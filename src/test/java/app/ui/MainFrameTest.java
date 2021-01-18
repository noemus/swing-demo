package app.ui;

import app.Main;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;

import static org.assertj.swing.core.BasicRobot.robotWithCurrentAwtHierarchy;
import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.assertj.swing.launcher.ApplicationLauncher.application;
import static org.junit.Assert.assertEquals;

public class MainFrameTest {
    private FrameFixture window;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() {
        application(Main.class).start();
        window = findFrame(MAIN_FRAME_MATCHER).using(robotWithCurrentAwtHierarchy());
    }

    @Test
    public void shouldAddHelloToTextPane() {
        toolbar().button(HELLO_BTN).click();

        assertEquals("Hello" + System.lineSeparator(), execute(() -> textPane().textBox().text()));
    }

    @Test
    public void shouldClearTextPane() {
        toolbar().button(HELLO_BTN).click();
        toolbar().button(CLEAR_BTN).click();

        assertEquals("", execute(() -> textPane().textBox().text()));
    }

    @After
    public void tearDown() {
        window.cleanUp();
    }

    private JPanelFixture toolbar() {
        return window.panel(TOOLBAR_MATCHER);
    }

    private JPanelFixture textPane() {
        return window.panel(TEXT_PANE_MATCHER);
    }

    private static final String HELLO_BTN = "Hello";
    private static final String CLEAR_BTN = "Clear";

    private static final GenericTypeMatcher<Frame> MAIN_FRAME_MATCHER = new GenericTypeMatcher<>(Frame.class) {
        @Override
        protected boolean isMatching(Frame frame) {
            return frame.isShowing();
        }
    };
    private static final GenericTypeMatcher<JPanel> TEXT_PANE_MATCHER = new GenericTypeMatcher<>(JPanel.class) {
        @Override
        protected boolean isMatching(JPanel panel) {
            return panel instanceof TextPane;
        }
    };
    private static final GenericTypeMatcher<JPanel> TOOLBAR_MATCHER = new GenericTypeMatcher<>(JPanel.class) {
        @Override
        protected boolean isMatching(JPanel panel) {
            return panel instanceof Toolbar;
        }
    };
}
