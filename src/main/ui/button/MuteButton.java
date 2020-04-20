package ui.button;

import ui.App;

import javax.swing.*;

// Represents a button that mutes/unmutes the app
public class MuteButton extends ImageButton {
    private static final String MUTE_ICON = "./data/icon/mutedIcon.png";
    private static final String UNMUTE_ICON = "./data/icon/notMutedIcon.png";
    private static final String MUTE_TEXT = "Click to unmute";
    private static final String UNMUTE_TEXT = "Click to mute";
    private static final float MIN_VOLUME = -79; // min volume > actual min volume
    private float prevVolume = 0;
    private static final float ERROR = (float) 0.01;

    // EFFECTS: initializes a MuteButton with default params (unmuted)
    public MuteButton() {
        super(new ImageIcon(UNMUTE_ICON));
        MuteButton instance = this;
        setToolTipText(UNMUTE_TEXT);
        addActionListener(e -> instance.onClick());
    }

    // MODIFIES: this
    // EFFECTS; changes the state of the button upon click
    @Override
    protected void onClick() {
        if (getToolTipText().equals(UNMUTE_TEXT)) {
            // mutes
            setIcon(new ImageIcon(MUTE_ICON));
            setToolTipText(MUTE_TEXT);
            App.getActiveApp().mute();

        } else {
            setIcon(new ImageIcon(UNMUTE_ICON));
            setToolTipText(UNMUTE_TEXT);
            App.getActiveApp().unmute();
        }
    }

    // MODIFIES: this
    // EFFECTS: changes the state of the button when volume is changed
    public void update() {
        float currentVolume = App.getActiveApp().getCurrentVolume();
        if (Math.abs(currentVolume - prevVolume) <= ERROR) {
            return;
        }
        if (getToolTipText().equals(UNMUTE_TEXT) && currentVolume <= MIN_VOLUME) {
            setIcon(new ImageIcon(MUTE_ICON));
            setToolTipText(MUTE_TEXT);
            App.getActiveApp().mute();
        } else if (getToolTipText().equals(MUTE_TEXT) && currentVolume > MIN_VOLUME) {
            setIcon(new ImageIcon(UNMUTE_ICON));
            setToolTipText(UNMUTE_TEXT);
            App.getActiveApp().unmute();
        }
        repaint();
        prevVolume = currentVolume;
    }
}
