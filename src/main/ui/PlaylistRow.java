package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Represents a Row in the PlaylistView (moved to different class)
public class PlaylistRow extends JPanel {
    private int index;
    private PopUpMenu popup;
    private static final Font BOLD_TEXT = new Font("Arial", Font.BOLD, 12);
    private static final Font NORMAL_TEXT = new Font("Arial", Font.PLAIN, 12);
    private static final Color DEFAULT_BACKGROUND = new Color(255, 255, 255);
    private JLabel leftSide;
    private JLabel rightSide;

    // EFFECTS: initializes PlaylistRow with songsInstance and song index
    //          throws IndexOutOfBoundsException if index is out of bounds
    public PlaylistRow(int index) {
        this.index = index;
        if (index > App.getActiveApp().getCurrentPlaylistLength()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        setLayout(new GridLayout(0, 2));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        leftSide = new JLabel(App.getActiveApp().getSongName(index));
        rightSide = new JLabel(App.getActiveApp().getSongTimeStamp(index));
        setBackground(DEFAULT_BACKGROUND);
        add(leftSide);
        add(rightSide);
        popup = new PopUpMenu(index);
        setComponentPopupMenu(popup);
        updateBoldStatus();
    }

    // MODIFIES: this
    // EFFECTS: Sets leftSide/rightSide to bold when playing
    private void updateBoldStatus() {
        if (App.getActiveApp().isSongPlaying(index)) {
            leftSide.setFont(BOLD_TEXT);
            rightSide.setFont(BOLD_TEXT);
        } else if (leftSide.getFont().isBold()) {
            leftSide.setFont(NORMAL_TEXT);
            rightSide.setFont(NORMAL_TEXT);
        }
    }

    // Represents the pop-up menu for interacting with a single row
    private class PopUpMenu extends JPopupMenu {
        int index;
        JMenuItem stop;
        JMenuItem play;

        // Initializes the PopUpMenu
        public PopUpMenu(int index) {
            this.index = index;
            play = new JMenuItem("Play");
            play.addMouseListener(new PlayClickListener(index));
            stop = new JMenuItem("Stop");
            stop.addMouseListener(new StopClickListener(index));
            JMenuItem remove = new JMenuItem("Remove");
            remove.addMouseListener(new RemoveClickListener(index));
            add(play);
            add(stop);
            add(remove);
        }

        // MODIFIES: this
        // EFFECTS: updates and disables specific menu items
        public void update() {
            if (App.getActiveApp().isSongPlaying(index)) {
                if (play.isEnabled()) {
                    play.setEnabled(false);
                    stop.setEnabled(true);
                }
            } else if (!play.isEnabled()) {
                play.setEnabled(true);
                stop.setEnabled(false);
            }
        }

        // Represents a mouse click listener for the remove option on popup menu
        public class RemoveClickListener extends MouseAdapter {
            private String name;

            // EFFECTS: Initializes the RemoveClickListener with a song to remove
            public RemoveClickListener(int index) {
                name = App.getActiveApp().getSongName(index);
            }

            @Override
            // MODIFIES: this
            // EFFECTS: removes the soundfile upon mouse click on popup menu
            public void mousePressed(MouseEvent e) {
                App.getActiveApp().remove(name);
            }
        }


        // Represents a mouse click listener for PopUpMenu
        public class PlayClickListener extends MouseAdapter {
            private String name;

            // EFFECTS: Initializes the PlayClickListener with a soundFile to play
            public PlayClickListener(int index) {
                name = App.getActiveApp().getSongName(index);
            }

            @Override
            // MODIFIES: this
            // EFFECTS: starts the sound file upon mouse click on popup menu
            public void mousePressed(MouseEvent e) {
                App.getActiveApp().play(name);
            }
        }

        // Represents a mouse click listener for PopUpMenu - the stop button
        public class StopClickListener extends MouseAdapter {
            private int index;

            // EFFECTS: Initializes the StopClickListener with a soundFile to play
            public StopClickListener(int index) {
                this.index = index;
            }

            @Override
            // MODIFIES: this
            // EFFECTS: stops the sound file when its playing upon mouse click on popup menu
            public void mousePressed(MouseEvent e) {
                App.getActiveApp().stop(index);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: updates popup menu and bold status
    public void update() {
        updateBoldStatus();
        popup.update();
    }
}
