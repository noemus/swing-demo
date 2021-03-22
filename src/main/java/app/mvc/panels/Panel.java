package app.mvc.panels;

import java.awt.*;

public record Panel(int index, Color color) {
    public String label() {
        return Integer.toString(index);
    }
}
