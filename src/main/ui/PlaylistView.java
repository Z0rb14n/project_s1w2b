package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

// Represents the view of all the playlists
public class PlaylistView extends JPanel {
    private ArrayList<PlaylistRow> playlistRows;
    public static final int DEFAULT_WIDTH = 640;
    public static final int DEFAULT_HEIGHT = 400;
    public static final Color DEFAULT_BACKGROUND = new Color(255, 255, 255);

    // EFFECTS: initializes AppPanel with starting conditions
    public PlaylistView() {
        playlistRows = new ArrayList<>();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setBackground(DEFAULT_BACKGROUND);
        populate();
    }

    // MODIFIES: this
    // EFFECTS: populates the inside of the JPanel
    private void populate() {
        addHeader();
        JPanel rows = new JPanel();
        rows.setBackground(Color.WHITE);
        int length = App.getActiveApp().getCurrentPlaylistLength();
        for (int i = 0; i < length; i++) {
            PlaylistRow row = new PlaylistRow(i);
            playlistRows.add(row);
            rows.add(row);
        }
        add(rows, BorderLayout.CENTER);
    }

    // MODIFIES: this
    // EFFECTS: adds a header to the top of the panel
    private void addHeader() {
        JPanel head = new JPanel(new GridLayout(0, 2));
        head.setBorder(new EmptyBorder(10, 20, 10, 20)); // add padding, like in HTML
        head.add(new JLabel("Song Location"));
        head.add(new JLabel("Time"));
        add(head, BorderLayout.PAGE_START);
    }

    // MODIFIES: this
    // EFFECTS: updates all labels to match the names/times of the weird Playlist in question
    public void update() {
        removeAll();
        playlistRows.clear();
        populate();
        for (PlaylistRow row : playlistRows) {
            row.update();
        }
        validate(); // cuz removeAll invalidates everything for some reason.
        repaint();
    }
}
