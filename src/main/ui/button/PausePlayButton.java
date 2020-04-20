package ui.button;

import ui.App;

import javax.swing.*;

// Represents the Play and the Stop button (i.e. they're the same one, if it)
public class PausePlayButton extends ImageButton {
    private static final String PLAY_ICON = "./data/icon/playIcon.png";
    private static final String PAUSE_ICON = "./data/icon/pauseIcon.png";
    private static final String PLAY_MSG = "Click to play";
    private static final String PAUSE_MSG = "Click to pause";

    // EFFECTS: Initializes the play button into the default state (i.e. not playing)
    public PausePlayButton() {
        super(new ImageIcon(PLAY_ICON));
        setToolTipText(PLAY_MSG);
        PausePlayButton instance = this;
        addActionListener(e -> instance.onClick());
    }

    // MODIFIES: this
    // EFFECTS: changes the state of the button upon click
    @Override
    protected void onClick() {
        if (App.getActiveApp().isPlaying()) {
            App.getActiveApp().pause();
            setToolTipText(PLAY_MSG);
            setIcon(new ImageIcon(PLAY_ICON));
        } else {
            int result = App.getActiveApp().playNext();
            if (result == App.PLAY_EMPTY) {
                JOptionPane.showMessageDialog(this, "There was nothing to play. LMAO");
            } else if (result == App.PLAY_SUCCESS) {
                setToolTipText(PAUSE_MSG);
                setIcon(new ImageIcon(PAUSE_ICON));
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: updates state of button if no songs are playing
    public void update() {
        if (!App.getActiveApp().isPlaying() && getToolTipText().equals(PAUSE_MSG)) {
            setToolTipText(PLAY_MSG);
            setIcon(new ImageIcon(PLAY_ICON));
        } else if (App.getActiveApp().isPlaying() && getToolTipText().equals(PLAY_MSG)) {
            setToolTipText(PAUSE_MSG);
            setIcon(new ImageIcon(PAUSE_ICON));
        }
    }
}
