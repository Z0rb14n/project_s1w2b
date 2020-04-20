package ui;

import javax.swing.*;
import java.awt.*;

// Represents the top part of the GUI in the music player
public class TopAppPanel extends JPanel {
    public static final int DEFAULT_WIDTH = AppPanel.DEFAULT_WIDTH;
    public static final int DEFAULT_HEIGHT = 30;
    public static final Color DEFAULT_BACKGROUND = new Color(255, 255, 255);
    private AppMenu am;
    private JLabel playlistName;

    // EFFECTS: Initializes TopAppPanel with AppMenu and PlaylistName
    public TopAppPanel() {
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setBackground(DEFAULT_BACKGROUND);
        am = new AppMenu();
        add(am);
        playlistName = new JLabel("Current Playlist: " + Playlists.MAIN);
    }

    // MODIFIES: this
    // EFFECTS: updates all components in JPanel
    public void update() {
        removeAll();
        playlistName = new JLabel("Current Playlist: " + App.getActiveApp().getCurrentlyUsed());
        am.update();
        add(am);
        add(playlistName);
        validate();
    }
}
