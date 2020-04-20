package ui.button;

import ui.App;

import javax.swing.*;

// Represents the stop button
public class StopButton extends ImageButton {
    private static String STOP_ICON = "./data/icon/stopIcon.png";
    private static String DISABLED_STOP_ICON = "./data/icon/stopSymbolDisabled.png";

    // EFFECTS: Initializes empty LoopButton with default image/state
    public StopButton() {
        super(new ImageIcon(STOP_ICON));
        StopButton instance = this;
        setToolTipText("Click to stop playback.");
        setDisabledIcon(new ImageIcon(DISABLED_STOP_ICON));
        addActionListener(e -> instance.onClick());
        setEnabled(false);
    }

    // MODIFIES: this
    // EFFECTS: disabled/enables the stop button when a soundFile is playing
    public void update() {
        if (App.getActiveApp().isPlaying() && !isEnabled()) {
            setEnabled(true);
        } else if (!App.getActiveApp().isPlaying() && isEnabled()) {
            setEnabled(false);
        }
        repaint();
    }

    // MODIFIES: this
    // EFFECTS: stops playback upon mouse click
    @Override
    protected void onClick() {
        App.getActiveApp().stop();
        setEnabled(false);
        repaint();
    }

}
