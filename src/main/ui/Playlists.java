package ui;

import exception.ParsingException;
import model.Songs;
import persistence.FileType;
import persistence.PlaylistParser;
import persistence.PlaylistWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

// Represents a group of playlists
public class Playlists {
    private HashMap<String, Songs> lists = new HashMap<>(); // I could also use Map, but I prefer HashMap
    public static final String MAIN = "Main";
    private String currentlyUsed = MAIN;
    //<editor-fold desc="RETURN VALUES">
    public static final int PLAY_DNE = -2;
    public static final int PLAY_EMPTY = -1;
    public static final int PLAY_SUCCESS = 0;
    public static final int PREV_DNE = -2;
    public static final int PREV_SUCCESS = 0;
    public static final int SKIP_SUCCESS = Songs.SKIPSTATUS_SUCCESSFUL;
    public static final int SKIP_FAIL = Songs.SKIPSTATUS_NOTPLAYING;
    //</editor-fold>

    // EFFECTS: initializes a main playlist
    public Playlists() {
        lists.put(MAIN, new Songs());
    }

    // MODIFIES: this
    // EFFECTS: jumps current playlist to given location
    public int jumpTo(long micros) {
        System.out.println("Jumping to " + micros + " microseconds...");
        int result = lists.get(currentlyUsed).jumpTo(micros);
        if (result == SKIP_SUCCESS) {
            System.out.println("Successfully jumped to location!");
        }
        return result;
    }

    // MODIFIES: this
    // EFFECTS: removes playlist with given name
    public void removePlaylist(String key) {
        if (key.equals(MAIN)) {
            System.out.println("Cannot remove main playlist.");
        } else if (!lists.containsKey(key)) {
            System.out.println("Playlist " + key + " not found.");
        } else {
            lists.remove(key);
            System.out.println("Playlist " + key + " has been removed.");
            currentlyUsed = MAIN;
        }
    }

    // MODIFIES: this
    // EFFECTS: plays previous song in active playlist
    public int playPrev() {
        Songs instance = lists.get(currentlyUsed);
        if (instance.isEmpty()) {
            return PREV_DNE;
        }
        int currentIndexPlaying = instance.indexPlaying();
        if (currentIndexPlaying == -1) {
            playPrevWhenStopped();
        } else {
            if (instance.get(currentIndexPlaying).getTimePosition() > 0) {
                jumpToZero();
            } else if (currentIndexPlaying == 0) {
                instance.play(instance.get(0).getString());
                jumpToZero();
            } else {
                instance.play(instance.get(currentIndexPlaying - 1).getString());
            }
        }
        return PREV_SUCCESS;
    }

    // MODIFIES: this
    // EFFECTS: plays previous song when player is paused/stopped
    private void playPrevWhenStopped() {
        Songs instance = lists.get(currentlyUsed);
        int lastPlayed = instance.getLastIndexPlayed();
        if (lastPlayed == -1) {
            instance.play(instance.get(0).getString());
        } else if (lastPlayed == 0) {
            instance.play(instance.get(0).getString());
            jumpToZero();
        } else if (instance.get(lastPlayed).isPaused() && instance.get(lastPlayed).getPausedTimePosition() > 0) {
            jumpToZero();
        } else {
            instance.play(instance.get(lastPlayed - 1).getString());
        }
    }

    // MODIFIES: this
    // EFFECTS: convenience function for jumpTo - jumps to zero micros
    public void jumpToZero() {
        jumpTo(0);
    }

    // MODIFIES: this
    // EFFECTS: makes new playlist and makes that currently used
    public void makeNew(String name) {
        if (lists.containsKey(name)) {
            System.out.println("Playlist " + name + " already exists.");
            return;
        }
        lists.put(name, new Songs());
        currentlyUsed = name;
        System.out.println("Playlist  " + name + " created.");
    }

    // MODIFIES: this
    // EFFECTS: makes a given playlist active
    public void switchTo(String name) {
        if (!lists.containsKey(name)) {
            System.out.println("Playlist " + name + " does not exist.");
            return;
        }
        currentlyUsed = name;
        System.out.println("Playlist " + name + "  now in focus.");
    }

    // MODIFIES: this
    // EFFECTS: clears current playlist
    public void clearCurrent() {
        App.getActiveApp().clearCurrent();
    }

    // MODIFIES: this
    // EFFECTS: resets the playlist
    public void reset() {
        for (String key : lists.keySet()) {
            if (!key.equals(MAIN)) {
                lists.get(key).disableLoopPlaylist();
                lists.get(key).noLoop();
                lists.get(key).stopAll();
                lists.remove(key);
                System.out.println("Playlist " + key + " has been removed.");
            }
        }
        lists.get(MAIN).stopAll();
        lists.get(MAIN).disableLoopPlaylist();
        lists.get(MAIN).noLoop();
        lists.get(MAIN).clear();
        lists.get(MAIN).unmute();
        System.out.println("Main playlist has been reset.");
    }

    // MODIFIES: this
    // EFFECTS: plays next song in active playlist
    public int playNext() {
        if (lists.get(currentlyUsed).getLength() == 0) {
            System.out.println("There is no song to play.");
            return PLAY_EMPTY;
        }
        lists.get(currentlyUsed).playNext();
        System.out.println("Playing next song...");
        return PLAY_SUCCESS;
    }

    // MODIFIES: this
    // EFFECTS: enables single-song-looping in active playlist
    public void loop() {
        System.out.println("Single song loop has been enabled!");
        lists.get(currentlyUsed).disableLoopPlaylist();
        lists.get(currentlyUsed).loop();
    }

    // MODIFIES: this
    // EFFECTS: disables single-song-looping in active playlist
    public void noLoop() {
        System.out.println("Single song loop has been disabled!");
        lists.get(currentlyUsed).noLoop();
    }

    // MODIFIES: this
    // EFFECTS: enables playlist-looping in active playlist
    public void enablePlaylistLoop() {
        System.out.println("Playlist loop has been enabled!");
        lists.get(currentlyUsed).noLoop();
        lists.get(currentlyUsed).loopPlaylist();
    }

    // MODIFIES: this
    // EFFECTS: disables single-song-looping in active playlist
    public void disablePlaylistLoop() {
        System.out.println("Playlist loop has been disabled!");
        lists.get(currentlyUsed).disableLoopPlaylist();
    }

    // EFFECTS: gets whether the main playlist is playing a song
    public boolean isPlaying() {
        return lists.get(currentlyUsed).indexPlaying() != -1;
    }

    // EFFECTS: gets volume of all playlists
    public float getVolume() {
        return lists.get(MAIN).getVolume();
    }

    // MODIFIES: this
    // EFFECTS: sets volume of all playlists.
    public void volumeAdjust(float val) {
        if (val >= Songs.MAX_VOLUME) {
            val = Songs.MAX_VOLUME;
            System.out.println("Volume over max gain setting. Switching to: " + val);
        } else if (val <= Songs.MIN_VOLUME) {
            val = Songs.MIN_VOLUME;
            System.out.println("Volume under min gain setting. Switching to: " + val);
        }
        for (Songs s : lists.values()) {
            s.setVolume(val);
        }
        System.out.println("Set volume to: " + val);
    }

    // MODIFIES: this
    // EFFECTS: sets volume of all playlists to lowest setting
    public void mute() {
        for (Songs s : lists.values()) {
            s.mute();
        }
        System.out.println("Muted all playlists.");
    }

    // MODIFIES: this
    // EFFECTS: sets volume of all playlists to lowest setting
    public void unmute() {
        for (Songs s : lists.values()) {
            s.unmute();
        }
        System.out.println("Unmuted all playlists.");
    }

    // EFFECTS: prints all songs in active playlists
    public void list() {
        System.out.println("List of songs: \n" + lists.get(currentlyUsed).list());
    }

    // EFFECTS: prints all names of playlists
    public void listPlaylists() {
        System.out.println("List of playlists: ");
        for (int i = 0; i < lists.keySet().toArray().length; i++) {
            System.out.print(lists.keySet().toArray()[i]);
            if (i != lists.keySet().toArray().length - 1) {
                System.out.println();
            }
        }
        System.out.println();
    }

    // EFFECTS: returns a list of all playlist names
    public ArrayList<String> listPlaylistNames() {
        return new ArrayList(lists.keySet());
    }

    // MODIFIES: this
    // EFFECTS: pauses playing song in active playlist
    public void pause() {
        boolean result = lists.get(currentlyUsed).pause();
        if (result) {
            System.out.println("Currently playing song has been paused!");
        } else {
            System.out.println("No song was playing.");
        }
    }

    // MODIFIES: this
    // EFFECTS: adds given path to song into active playlist
    //          returns value returned by Songs instance
    public int add(String s) {
        int result = lists.get(currentlyUsed).add(s);
        if (result == Songs.ADDSTATUS_SUCCESSFUL) {
            System.out.println(s + " added.");
        } else if (result == Songs.ADDSTATUS_EXISTS) {
            System.out.println(s + " already exists.");
        } else if (result == Songs.ADDSTATUS_UNSUPPORTED) {
            System.out.println(s + " is not supported.");
        } else if (result == Songs.ADDSTATUS_ERROR) {
            System.out.println("Could not add: " + s + ". It may not exist or be a random error.");
        }
        return result;
    }

    // MODIFIES: this
    // EFFECTS: removes the given file from active playlist
    public void remove(String c) {
        if (lists.get(currentlyUsed).remove(c)) {
            System.out.println(c + " removed.");
        } else {
            System.out.println("Could not remove " + c + ".");
        }
    }

    // MODIFIES: this
    // EFFECTS: removes file of given index (convenience method)
    public void remove(int index) {
        remove(lists.get(currentlyUsed).getString(index));
    }

    // MODIFIES: this
    // EFFECTS: stops all playing soundfiles in active playlist
    public void stop() {
        System.out.println("Stopping all sounds in current playlist...");
        lists.get(currentlyUsed).stopAll();
    }

    // MODIFIES: this
    // EFFECTS: plays sound file of given name in current playlist, if it exists
    public void play(String s) {
        int result = lists.get(currentlyUsed).play(s);
        if (result == Songs.PLAYSTATUS_NOTFOUND) {
            System.out.println("Could not play " + s);
        } else if (result == Songs.PLAYSTATUS_SUCCESSFUL) {
            System.out.println(s + " is now playing.");
        }
    }

    // EFFECTS: returns active Songs instance
    public Songs getActiveSongsInstance() {
        return lists.get(currentlyUsed);
    }

    // MODIFIES: this
    // EFFECTS: stops all loops/sound files
    public void cleanup() {
        for (Songs s : lists.values()) {
            s.noLoop();
            s.disableLoopPlaylist();
            s.stopAll();
        }
        System.out.println("All sounds stopped!");
    }

    // MODIFIES: this
    // EFFECTS: adds a new playlist of given name to this
    public void open(String name) {
        PlaylistParser pp = new PlaylistParser();
        try {
            Songs s = pp.getSongs(name);
            String actualName = new File(name).getName();
            String testName = new File(name).getName();
            int copyNum = 0;
            while (lists.containsKey(testName)) {
                copyNum++;
                testName = actualName + copyNum;
            }
            lists.put(testName, s);
        } catch (FileNotFoundException e) {
            System.out.println("File " + name + " not found.");
        } catch (ParsingException e) {
            System.out.println("Potentially invalid playlist file: " + name);
        }
    }

    // EFFECTS: returns name of playlist currently used
    public String getCurrentlyUsed() {
        return currentlyUsed;
    }

    //<editor-fold desc="Playlist persistence">

    // EFFECTS: saves all playlists to file.
    public void saveAll() {
        PlaylistWriter pw = new PlaylistWriter();
        try {
            for (String key : lists.keySet()) {
                pw.write(lists.get(key), key);
            }
        } catch (IOException e) {
            System.out.println("Could not write to files.");
        }
    }

    // EFFECTS: saves given playlist to file.
    public void save() {
        PlaylistWriter pw = new PlaylistWriter();
        try {
            pw.write(lists.get(currentlyUsed), currentlyUsed);
        } catch (IOException e) {
            System.out.println("Could not write " + currentlyUsed + " to file.");
        }
    }

    // EFFECTS: saves active playlist to file of given name
    public void saveAs(String name) {
        PlaylistWriter pw = new PlaylistWriter();
        try {
            pw.write(lists.get(currentlyUsed), name);
        } catch (IOException e) {
            System.out.println("Could not write " + name + " to file.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid name: " + name);
        }
    }

    // EFFECTS: saves active playlist to file of given file type
    public void saveWithExt(String type) {
        PlaylistWriter pw = new PlaylistWriter();
        try {
            if (type.compareToIgnoreCase(".m3u") != 0 && type.compareToIgnoreCase(".m3u8") != 0) {
                if (type.compareToIgnoreCase(".pls") != 0) {
                    System.out.println("Invalid file extension: " + type);
                } else {
                    pw.write(lists.get(currentlyUsed), currentlyUsed, FileType.PLS);
                }
            } else {
                pw.write(lists.get(currentlyUsed), currentlyUsed, FileType.EXTM3U);
            }
        } catch (IOException e) {
            System.out.println("Could not write " + currentlyUsed + " to file.");
        }
    }

    // EFFECTS: saves active playlist to given file (but not default location)
    public void saveAsNonDefault(String name) {
        PlaylistWriter pw = new PlaylistWriter();
        try {
            if (PlaylistWriter.getFileExtension(name) == null) {
                pw.write(lists.get(currentlyUsed), name, PlaylistWriter.DEFAULT_FILETYPE, false);
            } else {
                pw.write(lists.get(currentlyUsed), name, PlaylistWriter.getFileExtension(name), false);
            }
        } catch (IOException e) {
            System.out.println("Could not write " + name + " to file.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid name: " + name);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Convenience Methods">

    // EFFECTS: returns last index played (convenience method)
    public int getLastIndexPlayed() {
        return lists.get(currentlyUsed).getLastIndexPlayed();
    }

    // EFFECTS: returns current index of song playing, -1 if none (convenience method)
    public int getCurrentIndexPlaying() {
        return lists.get(currentlyUsed).indexPlaying();
    }

    // EFFECTS: returns whether or not the current Songs instance is empty (convenience method)
    public boolean isCurrentPlaylistEmpty() {
        return lists.get(currentlyUsed).isEmpty();
    }

    // EFFECTS: gets current position in song in microseconds (convenience method)
    public long getCurrentMicrosecondPosition() {
        return lists.get(currentlyUsed).getCurrentPosition();
    }

    // FLAG --------------------------------------------------------------------------------

    // EFFECTS: gets current song's length in microseconds (convenience method)
    public long getCurrentMicrosecondLength() {
        int index = getCurrentIndexPlaying();
        return lists.get(currentlyUsed).get(index).length();
    }

    // EFFECTS: gets song's length in microseconds (convenience method)
    public long getMicrosecondLength(int index) {
        return lists.get(currentlyUsed).get(index).length();
    }

    // EFFECTS: gets last last played's paused time position
    public long getLastPausedMicrosecondPosition() {
        int index = getLastIndexPlayed();
        return lists.get(currentlyUsed).get(index).getPausedTimePosition();
    }

    // EFFECTS: returns time stamp of song of given index
    public String getSongTimeStamp(int index) {
        return lists.get(currentlyUsed).get(index).stringLength();
    }

    // EFFECTS: returns whether current song is playing
    public boolean isCurrentSongPlaying(int index) {
        return lists.get(currentlyUsed).get(index).isPlaying();
    }

    // END FLAG

    // EFFECTS: gets current playlist's length (i.e. number of songs)
    public int getCurrentPlaylistLength() {
        return lists.get(currentlyUsed).getLength();
    }

    // EFFECTS: returns whether or not the current playlist is paused
    public boolean isCurrentPlaylistPaused() {
        return lists.get(currentlyUsed).isPaused();
    }

    // EFFECTS: returns the song name of the given index in the current playlist (convenience method)
    public String getSongName(int index) {
        return lists.get(currentlyUsed).getString(index);
    }

    //</editor-fold>

}
