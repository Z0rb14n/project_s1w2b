package ui.button;

import javax.swing.*;
import java.awt.*;

// Abstract ImageButton to reduce code duplication
public abstract class ImageButton extends JButton {
    protected static final int DEFAULT_WIDTH = 31;
    protected static final int DEFAULT_HEIGHT = 31;
    protected static final Color DEFAULT_COLOR = new Color(0, 0, 0, 0);

    // EFFECTS: initializes the button to have an image icon as the image
    protected ImageButton(ImageIcon ii) {
        super(null, ii);
        setMargin(new Insets(0, 0, 0, 0)); // disables margin to make image whole button
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setBackground(DEFAULT_COLOR);
        setBorderPainted(false);
    }

    // MODIFIES: this
    // EFFECTS: a method that is run when the button is pressed
    protected abstract void onClick();
}
