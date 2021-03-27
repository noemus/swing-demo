package app.suggestions.ui;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.fixture.JLabelFixture;
import org.assertj.swing.fixture.JPanelFixture;

import javax.swing.*;
import java.util.Optional;

public class PanelFinder extends JPanelFixture {
    public PanelFinder(JPanelFixture delegate) {
        super(delegate.robot(), delegate.target());
    }

    public Optional<JPanelFixture> findPanel(GenericTypeMatcher<JPanel> matcher) {
        return finder().findAll(matcher)
                       .stream()
                       .map(panel -> new JPanelFixture(robot(), panel))
                       .findFirst();
    }

    public Optional<JLabelFixture> findLabel(String text) {
        return findLabel(JLabelMatcher.withText(text));
    }

    public Optional<JLabelFixture> findLabel(GenericTypeMatcher<JLabel> matcher) {
        return finder().findAll(matcher)
                       .stream()
                       .map(label -> new JLabelFixture(robot(), label))
                       .findFirst();
    }
}
