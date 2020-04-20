package ui.button;

import ui.App;

import javax.swing.*;

import static ui.button.LoopButton.State.*;

// Represents that LoopButton
public class LoopButton extends ImageButton {
    private static final String DARK_LOOP_ICON = "./data/icon/darkLoopIcon.png";
    private static final String LOOP_ICON = "./data/icon/loopIcon.png";
    private static final String LOOP_ONE_ICON = "./data/icon/loopIconOne.png";

    private State currentState = NOLOOP;

    // EFFECTS: Initializes empty LoopButton with default image/state
    public LoopButton() {
        super(new ImageIcon(DARK_LOOP_ICON));
        LoopButton instance = this;
        setToolTipText("Click to disable/enable looping");
        addActionListener(e -> instance.onClick());
    }

    // MODIFIES: this
    // EFFECTS: changes the state of the button upon click
    @Override
    protected void onClick() {
        if (currentState == NOLOOP) {
            setIcon(new ImageIcon(LOOP_ICON));
            App.getActiveApp().playlistLoop();
            currentState = LOOP;
        } else if (currentState == LOOP) {
            setIcon(new ImageIcon(LOOP_ONE_ICON));
            currentState = LOOP_ONE;
            App.getActiveApp().loop();
        } else if (currentState == LOOP_ONE) {
            setIcon(new ImageIcon(DARK_LOOP_ICON));
            currentState = NOLOOP;
            App.getActiveApp().noLoop();
        }
        repaint();
    }

    // Represents current state of LoopButton
    public enum State {
        NOLOOP, LOOP, LOOP_ONE
    }

}
