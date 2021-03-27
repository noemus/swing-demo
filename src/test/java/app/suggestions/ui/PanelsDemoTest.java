package app.suggestions.ui;

import app.mvc.PanelsDemo;
import app.mvc.panels.PanelContainer;
import app.mvc.panels.PanelToolbar;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

import static app.suggestions.ui.Wait.waitFor;
import static org.assertj.swing.core.BasicRobot.robotWithCurrentAwtHierarchy;
import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

public class PanelsDemoTest {
    private FrameFixture window;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() {
        application(PanelsDemo.class).start();
        window = findFrame(MAIN_FRAME_MATCHER).using(robotWithCurrentAwtHierarchy());
    }

    @Test
    public void shouldAddColorBox() {
        toolbar().button(ADD_BTN).click();

        waitFor("Label '0' is present")
                .withTimeout(2000L)
                .until(() -> panels().findLabel("0").isPresent());

        toolbar().button(ADD_BTN).click();

        waitFor("Label '1' is present")
                .withTimeout(2000L)
                .until(() -> panels().findLabel("1").isPresent());
    }

    @Test
    public void shouldReset() {
        toolbar().button(ADD_BTN).click();
        toolbar().button(ADD_BTN).click();

        waitFor("Label '1' is present")
                .until(() -> panels().findLabel("1").isPresent());

        toolbar().button(RESET_BTN).click();

        window.dialog().button(JButtonMatcher.withText("OK")).click();

        waitFor("No Label is present")
                .until(() -> panels()
                        .findLabel(JLabelMatcher.withText(Pattern.compile("[01]")).andShowing())
                        .isEmpty());
    }

    @After
    public void tearDown() {
        window.cleanUp();
    }

    private JPanelFixture toolbar() {
        return window.panel(TOOLBAR_MATCHER);
    }

    private PanelFinder panels() {
        return new PanelFinder(window.panel(PANELS_MATCHER));
    }

    private static final String ADD_BTN = "Add";
    private static final String RESET_BTN = "Reset";

    private static final GenericTypeMatcher<Frame> MAIN_FRAME_MATCHER = new GenericTypeMatcher<>(Frame.class, true) {
        @Override
        protected boolean isMatching(Frame frame) {
            return frame.isShowing();
        }
    };
    private static final GenericTypeMatcher<JPanel> PANELS_MATCHER = new GenericTypeMatcher<>(JPanel.class, true) {
        @Override
        protected boolean isMatching(JPanel panel) {
            return panel instanceof PanelContainer;
        }
    };
    private static final GenericTypeMatcher<JPanel> TOOLBAR_MATCHER = new GenericTypeMatcher<>(JPanel.class, true) {
        @Override
        protected boolean isMatching(JPanel panel) {
            return panel instanceof PanelToolbar;
        }
    };

}
