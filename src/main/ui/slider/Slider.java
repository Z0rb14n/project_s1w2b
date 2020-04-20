package ui.slider;

import javax.swing.*;
import java.awt.*;

// Represents a Slider of anything (e.g. Time/Volume)
public abstract class Slider extends JSlider {
    protected static final int RESOLUTION = 1000;
    protected static final int HEIGHT = 30;

    // EFFECTS: initializes the slider with default height/resolution
    protected Slider(int length) {
        super(0, RESOLUTION);
        setPreferredSize(new Dimension(length, HEIGHT));
    }
}
