package ui;

import model.Songs;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;

// Represents the App Menu in the App Panel
public class AppMenu extends JMenuBar {

    private FileNameExtensionFilter m3uFilter;
    private FileNameExtensionFilter plsFilter;
    private FileNameExtensionFilter playlistFilter;
    private FileNameExtensionFilter supportedAudio;
    private JMenu delPlaylist;
    private JMenu switPlaylist;
    private int prevSize = 0; // previous size of delPlaylist
    private static final String ADD_ERROR_MSG = "Could not add song ";
    private static final String ADD_UNSUP_MSG = "Unsupported song ";
    private static final String ADD_EXIST_MSG = "Song already in playlist ";
    private int prevSizeSwit = 0;
    private String prevPlaylist = Playlists.MAIN;

    // EFFECTS: Initialize appMenu with default menu options
    public AppMenu() {
        setupFileFilters();
        setupFileOption();
        setupPlaylistOption();
    }

    // TODO MENU OPTION ACCELERATORS

    // MODIFIES: this
    // EFFECTS: sets up file filters
    private void setupFileFilters() {
        m3uFilter = new FileNameExtensionFilter("M3U Playlists", "m3u", "m3u8");
        plsFilter = new FileNameExtensionFilter("PLS Playlists", "pls");
        playlistFilter = new FileNameExtensionFilter("Playlists", "m3u", "m3u8", "pls");
        supportedAudio = new FileNameExtensionFilter("Supported audio formats", "wav", "au", "aiff");
    }

    // MODIFIES: this
    // EFFECTS: sets up File menu option
    private void setupFileOption() {
        AppMenu appMenuInstance = this;
        JMenu openFile = new JMenu("Open");
        JMenu fileMenu = new JMenu("File");
        JMenuItem openSong = new JMenuItem("Song");
        openSong.addActionListener(e -> appMenuInstance.doFileChooser());
        openFile.add(openSong);
        openFile.add(setupOpenPlaylistOption());
        JMenuItem saveAs = new JMenuItem("Save as");
        saveAs.addActionListener(e -> appMenuInstance.doFileSaver());
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(e -> appMenuInstance.defaultFileSave());
        JMenuItem clear = new JMenuItem("Clear current playlist");
        clear.addActionListener(e -> appMenuInstance.clear());
        JMenuItem reset = new JMenuItem("Reset");
        reset.addActionListener(e -> appMenuInstance.reset());
        fileMenu.add(openFile);
        fileMenu.add(save);
        fileMenu.add(saveAs);
        fileMenu.add(clear);
        fileMenu.add(reset);
        add(fileMenu);
    }

    // MODIFIES: this
    // EFFECTS: resets the app after an input dialog
    private void reset() {
        final String message = "Buddy, are you sure you wanna do that?";
        int result = JOptionPane.showConfirmDialog(this, message, "Reset", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            App.getActiveApp().reset();
        } else {
            System.out.println("Lol the user cancelled the reset.");
        }
    }

    // MODIFIES: this
    // EFFECTS: clears the currently active songs instance
    private void clear() {
        App.getActiveApp().clearCurrent();
    }

    // EFFECTS: returns Open/Playlist menu option
    private JMenuItem setupOpenPlaylistOption() {
        AppMenu appMenuInstance = this;
        JMenuItem openPlaylist = new JMenuItem("Playlist");
        openPlaylist.addActionListener(e -> appMenuInstance.doPlaylistChooser());
        return openPlaylist;
    }

    // MODIFIES: this
    // EFFECTS: sets up playlist menu option
    private void setupPlaylistOption() {
        AppMenu appMenuInstance = this;
        JMenu playlistMenu = new JMenu("Playlist");
        JMenuItem newPlaylist = new JMenuItem("New");
        newPlaylist.addActionListener(e -> appMenuInstance.runNewPlaylist());
        delPlaylist = new JMenu("Delete");
        switPlaylist = new JMenu("Switch to");
        playlistMenu.add(newPlaylist);
        playlistMenu.add(delPlaylist);
        playlistMenu.add(switPlaylist);
        switPlaylist.setEnabled(false);
        add(playlistMenu);
    }

    // MODIFIES: this
    // EFFECTS: switches to the playlist of given name
    private void switchPlaylist(String name) {
        App.getActiveApp().switchTo(name);
    }

    // MODIFIES: this
    // EFFECTS: updates delPlaylist list of playlists
    private void updateDelPlaylist() {
        AppMenu appMenuInstance = this;
        ArrayList<String> names = App.getActiveApp().listPlaylistNames();
        if (names.size() == prevSize) {
            return;
        }
        prevSize = names.size();
        if (names.size() == 1) {
            // only main
            delPlaylist.setEnabled(false);
        } else {
            delPlaylist.setEnabled(true);
            delPlaylist.removeAll();
            for (String name : names) {
                if (name.equals(Playlists.MAIN)) {
                    continue;
                }
                JMenuItem item = new JMenuItem(name);
                item.addActionListener(e -> appMenuInstance.remPlaylist(name));
                delPlaylist.add(item);
            }
            delPlaylist.validate();
        }
    }

    // MODIFIES: this
    // EFFECTS: updates switchToPlaylist list of items to switch to
    private void updateSwitPlaylist() {
        AppMenu appMenuInstance = this;
        ArrayList<String> names = App.getActiveApp().listPlaylistNames();
        if (names.size() == prevSizeSwit && App.getActiveApp().getCurrentlyUsed().equals(prevPlaylist)) {
            return;
        }
        prevPlaylist = App.getActiveApp().getCurrentlyUsed();
        prevSizeSwit = names.size();
        if (names.size() == 1) {
            // only main
            switPlaylist.setEnabled(false);
        } else {
            switPlaylist.setEnabled(true);
            switPlaylist.removeAll();
            for (String name : names) {
                if (name.equals(App.getActiveApp().getCurrentlyUsed())) {
                    continue;
                }
                JMenuItem item = new JMenuItem(name);
                item.addActionListener(e -> appMenuInstance.switchPlaylist(name));
                switPlaylist.add(item);
            }
            switPlaylist.validate();
        }
    }

    // MODIFIES: this
    // EFFECTS: updates delPlaylist list of items to delete
    public void update() {
        updateDelPlaylist();
        updateSwitPlaylist();
    }

    // MODIFIES: this
    // EFFECTS: removes playlist of given name in app
    private void remPlaylist(String name) {
        App.getActiveApp().remPlaylist(name);
    }

    // MODIFIES: this
    // EFFECTS: creates a new playlist of given input name in dialog box
    private void runNewPlaylist() {
        String title = "Create New Playlist";
        String message = "What is the playlist name?";
        String response = JOptionPane.showInputDialog(this, message, title, JOptionPane.QUESTION_MESSAGE);
        if (response != null) {
            ArrayList<String> names = App.getActiveApp().listPlaylistNames();
            if (names.contains(response)) {
                JOptionPane.showMessageDialog(this, "Playlist " + response + " already exists.");
                return;
            }
            App.getActiveApp().newPlaylist(response);
        }
    }

    // MODIFIES: this
    // EFFECTS: performs an addition of a file from a file dialog chooser
    private void doFileChooser() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(supportedAudio);
        jfc.setMultiSelectionEnabled(false);
        if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            final int result = App.getActiveApp().add(file.getPath());
            if (result == Songs.ADDSTATUS_ERROR) {
                JOptionPane.showMessageDialog(this, ADD_ERROR_MSG, "Error", JOptionPane.ERROR_MESSAGE);
            } else if (result == Songs.ADDSTATUS_UNSUPPORTED) {
                JOptionPane.showMessageDialog(this, ADD_UNSUP_MSG, "Error", JOptionPane.ERROR_MESSAGE);
            } else if (result == Songs.ADDSTATUS_EXISTS) {
                JOptionPane.showMessageDialog(this, ADD_EXIST_MSG, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: performs the opening of a playlist from a file dialog chooser
    private void doPlaylistChooser() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(playlistFilter);
        jfc.setMultiSelectionEnabled(false);
        if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            App.getActiveApp().open(file.getPath());
        }
    }

    // MODIFIES: this
    // EFFECTS: saves the file to given location
    private void doFileSaver() {
        JFileChooser jfc = new JFileChooser();
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.addChoosableFileFilter(m3uFilter);
        jfc.setFileFilter(m3uFilter);
        jfc.addChoosableFileFilter(plsFilter);
        if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            saveFile(jfc.getSelectedFile(), jfc.getFileFilter().toString());
        }
    }

    // EFFECTS: saves the given file if possible.
    private void saveFile(File file, String selectedFilter) {
        if (selectedFilter.equals(m3uFilter.toString())) {
            if (!m3uFilter.accept(file)) {
                if (plsFilter.accept(file)) {
                    JOptionPane.showMessageDialog(this, "Expected M3U file and gotten .PLS file.");
                } else {
                    App.getActiveApp().saveAsNonDefault(file.getPath() + ".m3u");
                }
            } else {
                App.getActiveApp().saveAsNonDefault(file.getPath());
            }
        } else {
            if (!plsFilter.accept(file)) {
                if (m3uFilter.accept(file)) {
                    JOptionPane.showMessageDialog(this, "Expected PLS file and gotten .M3U file.");
                } else {
                    App.getActiveApp().saveAsNonDefault(file.getPath() + ".pls");
                }
            } else {
                App.getActiveApp().saveAsNonDefault(file.getPath());
            }
        }
    }

    // EFFECTS: saves a file to default location (i.e. playlistName.default_file_ext)
    private void defaultFileSave() {
        App.getActiveApp().save();
    }
}
