package model;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.ArrayList;
import java.util.Iterator;

// represents a cluster of songs (e.g. a playlist)
public class Songs implements Iterable<SoundFile> {

    private ArrayList<SoundFile> files;
    public static final int PLAYSTATUS_NOTFOUND = 2;
    public static final int PLAYSTATUS_SUCCESSFUL = 0;
    public static final int ADDSTATUS_SUCCESSFUL = 0;
    public static final int ADDSTATUS_ERROR = 2;
    public static final int ADDSTATUS_EXISTS = 3;
    public static final int ADDSTATUS_UNSUPPORTED = 4;
    public static final int SKIPSTATUS_SUCCESSFUL = 0;
    public static final int SKIPSTATUS_NOTPLAYING = 1;
    public static final float MAX_VOLUME = (float) 6.0206;
    public static final float MIN_VOLUME = (float) -80;
    private static final long PLAYLIST_LOOP_ACC = 200;
    private float volume = 0; // volume could be static if the volume stays the same across all playlists
    private boolean isPaused = false;
    private boolean singleSongLooping = false;
    private boolean playlistLooping = false;
    private boolean isMuted = false;
    private int lastIndexPlayed = -1;
    private Thread playlistLooper;

    // EFFECTS: initializes empty songs list.
    public Songs() {
        files = new ArrayList<>();
    }

    // EFFECTS: gets the string name of sound file index.
    public String getString(int i) {
        return files.get(i).getString();
    }

    // EFFECTS: gets the soundfile of given index
    public SoundFile get(int i) {
        return files.get(i);
    }

    // EFFECTS: get size of sound files playlist
    public int getLength() {
        return files.size();
    }

    // MODIFIES: this
    // EFFECTS: jumps to specific position in currently playing song
    public int jumpTo(long micros) {
        if (lastIndexPlayed == -1 && files.size() == 0) {
            return SKIPSTATUS_NOTPLAYING;
        }
        int indexPlaying = indexPlaying();
        int indexToPlay = indexPlaying == -1 ? lastIndexPlayed == -1 ? 0 : lastIndexPlayed : indexPlaying;
        if (!files.get(indexToPlay).isPlaying()) {
            stopAll();
            files.get(indexToPlay).play();
        }
        files.get(indexToPlay).jumpTo(micros);
        lastIndexPlayed = indexToPlay;
        return SKIPSTATUS_SUCCESSFUL;
    }

    // EFFECTS: gets last played index
    public int getLastIndexPlayed() {
        return lastIndexPlayed;
    }

    // MODIFIES: this
    // EFFECTS: changes the volume to input value
    public void setVolume(float v) {
        v = v >= MAX_VOLUME ? MAX_VOLUME : v <= MIN_VOLUME ? MIN_VOLUME : v;
        volume = v;
        for (SoundFile sf : files) {
            sf.setVolume(v);
        }
    }

    // MODIFIES: this
    // EFFECTS: mutes all sound files.
    public void mute() {
        isMuted = true;
        for (SoundFile sf : files) {
            sf.mute();
        }
    }

    // EFFECTS: gets current position in sound file
    public long getCurrentPosition() {
        int index = indexPlaying();
        if (index == -1) {
            return -1;
        }
        return files.get(index).getTimePosition();
    }

    // MODIFIES: this
    // EFFECTS: unmutes all sound files.
    public void unmute() {
        isMuted = false;
        for (SoundFile sf : files) {
            sf.unmute();
        }
    }

    // EFFECTS: gets current volume, assuming volume wasn't changed
    public float getVolume() {
        return volume;
    }

    // MODIFIES: this
    // EFFECTS: plays the given sound after stopping all other sounds.
    //          returns PLAYSTATUS_SUCCESSFUL if it could play, PLAYSTATUS_NOTFOUND if non-existent
    public int play(String c) {
        stopAll();
        isPaused = false;
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getString().equals(c)) {
                files.get(i).play();
                lastIndexPlayed = i;
                return PLAYSTATUS_SUCCESSFUL;
            }
        }
        return PLAYSTATUS_NOTFOUND;
    }

    // EFFECTS: converts all the songs to a string of names
    public String list() {
        String result = "";
        for (int i = 0; i < files.size(); i++) {
            result += files.get(i).getString();
            if (!(i == files.size() - 1)) {
                result += "\n";
            }
        }
        return result;
    }

    // MODIFIES: this
    // EFFECTS: pauses the currently playing song
    //          returns true if successful, false if no song playing
    public boolean pause() {
        for (SoundFile s : files) {
            if (s.isPlaying()) {
                s.pause();
                isPaused = true;
                return true;
            }
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: stops all sounds from playing.
    public void stopAll() {
        for (SoundFile s : files) {
            s.stop();
        }
        isPaused = false;
    }

    // MODIFIES: this
    // EFFECTS: adds a soundfile to playlist
    //          returns ADDSTATUS_EXISTS if file already exists in playlist
    //          returns ADDSTATUS_UNSUPPORTED if file is not supported
    //          returns ADDSTATUS_ERROR if not found or Java could not create another audio instance
    public int add(String c) {
        for (SoundFile s : files) {
            if (s.getString().equals(c)) {
                return ADDSTATUS_EXISTS;
            }
        }
        try {
            SoundFile sf = new SoundFile(c);
            if (isMuted) {
                sf.mute();
            }
            files.add(sf);
            return ADDSTATUS_SUCCESSFUL;
        } catch (UnsupportedAudioFileException e) {
            return ADDSTATUS_UNSUPPORTED;
        } catch (Exception e) {
            //e.printStackTrace();
            return ADDSTATUS_ERROR;
        }
    }

    // MODIFIES: this
    // EFFECTS: removes soundfile from playlist, returns true if done, false if impossible
    public boolean remove(String s) {
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getString().equals(s)) {
                files.get(i).stop();
                files.remove(i);
                return true;
            }
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: clears the playlist
    public void clear() {
        files.clear();
    }

    // MODIFIES: this
    // EFFECTS: enables looping of one song
    public void loop() {
        singleSongLooping = true;
        for (SoundFile sf : files) {
            sf.loop();
        }
    }

    // MODIFIES: this
    // EFFECTS: disables looping of one song
    public void noLoop() {
        singleSongLooping = false;
        for (SoundFile sf : files) {
            sf.noLoop();
        }
    }

    // EFFECTS: gets index of song playing, -1 if none
    public int indexPlaying() {
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).isPlaying()) {
                return i;
            }
        }
        return -1;
    }

    // MODIFIES: this
    // EFFECTS: loops the playlist
    public void loopPlaylist() {
        playlistLooping = true;
        createAutoPlayer(indexPlaying());
        playlistLooper.start();
    }

    // MODIFIES: this
    // EFFECTS: creates a thread that loops the playlist
    private void createAutoPlayer(int startingIndex) {
        Songs ref = this;
        playlistLooper = new Thread() {
            @Override
            public void run() {
                int startIndex = startingIndex;
                while (ref.isPlaylistLooping()) {
                    if (ref.indexPlaying() == -1 && !ref.isEmpty()) {
                        if (startIndex == ref.files.size() - 1) {
                            startIndex = 0;
                        } else {
                            startIndex++;
                        }
                        play(ref.files.get(startIndex).getString());
                    }
                    try {
                        sleep(PLAYLIST_LOOP_ACC);
                    } catch (InterruptedException e) {
                        System.err.print("WARNING: playlistLooper interrupted.");
                    }
                }
            }
        };
    }

    // EFFECTS: returns whether the playlist is looping
    public boolean isPlaylistLooping() {
        return playlistLooping;
    }

    // EFFECTS: returns whether playlist is empty
    public boolean isEmpty() {
        return files.isEmpty();
    }

    // MODIFIES: this
    // EFFECTS: disables looping of playlist
    public void disableLoopPlaylist() {
        if (playlistLooper != null) {
            playlistLooper.interrupt();
            playlistLooper = null;
        }
        playlistLooping = false;
    }

    // MODIFIES: this
    // EFFECTS: plays next song in playlist
    public void playNext() {
        if (files.size() == 0) {
            return;
        }
        int index = lastIndexPlayed;
        stopAll();
        if (index == -1 || index == files.size() - 1) {
            files.get(0).play();
            lastIndexPlayed = 0;
        } else {
            files.get(index + 1).play();
            lastIndexPlayed = index + 1;
        }
        isPaused = false;
    }

    // EFFECTS: gets status of single song looping
    public boolean isSingleSongLooping() {
        return singleSongLooping;
    }

    // EFFECTS: returns whether the songs instance is paused
    public boolean isPaused() {
        return isPaused;
    }

    @Override
    // EFFECTS: returns an iterator for the files listed
    public Iterator<SoundFile> iterator() {
        return files.iterator();
    }
}
