package model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

// represents a sound file (e.g. something that can be played)
public class SoundFile {
    private Clip soundClip; // sound clip from java API
    private AudioInputStream wavStream; // audio input stream - i.e. file stream reads from the file
    private File file;
    private String internalString;
    private long timePosition = 0;
    private boolean isPaused = false;
    private boolean looping = false;

    // EFFECTS: initializes internal string, and sound clips
    //          throws illegalArgumentException if file does not exist
    //          throws UnsupportedAudioFileException if the file is not supported or not an audio file
    //          throws LineUnavailableException if Java cannot create an audio clip instance
    public SoundFile(String c) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        internalString = c;
        file = new File(c);
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + c);
        }
        soundClip = AudioSystem.getClip();
        wavStream = AudioSystem.getAudioInputStream(file);
        soundClip.open(wavStream);
    }

    // EFFECTS: returns length of string as minutes:seconds
    public String stringLength() {
        return convertMicrosecondsToTimeStamp(soundClip.getMicrosecondLength());
    }

    public static final String convertMicrosecondsToTimeStamp(long micros) {
        long minutes = Math.floorDiv(micros, 60000000);
        long seconds = Math.floorMod(Math.floorDiv(micros, 1000000), 60);
        if (seconds >= 10) {
            return minutes + ":" + seconds;
        } else {
            return minutes + ":0" + seconds;
        }
    }

    // EFFECTS: returns internal string
    public String getString() {
        return internalString;
    }

    // EFFECTS: returns a new file instance of the sound file in question
    public File getFile() {
        return new File(internalString);
    }

    // MODIFIES: this
    // EFFECTS: plays the file
    public void play() {
        if (!isPaused) {
            soundClip.setFramePosition(0);
        } else {
            soundClip.setMicrosecondPosition(timePosition);
        }
        if (!looping) {
            soundClip.loop(0);
        } else {
            soundClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        timePosition = 0;
        isPaused = false;
        soundClip.start();
    }

    // MODIFIES: this
    // EFFECTS: jumps to specific position in soundFile
    public void jumpTo(long micros) {
        soundClip.setMicrosecondPosition(micros);
    }

    // MODIFIES: this
    // EFFECTS: stops the file
    public void stop() {
        soundClip.stop();
    }

    // EFFECTS: returns whether sound file is playing
    public boolean isPlaying() {
        return soundClip.isRunning();
    }

    // EFFECTS: returns whether sound file is paused
    public boolean isPaused() {
        return isPaused;
    }

    // EFFECTS: returns whether the sound file is looping
    public boolean isLooping() {
        return looping;
    }

    // EFFECTS: returns current volume
    public float getVolume() {
        FloatControl volumeControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
        return volumeControl.getValue();
    }

    // MODIFIES: this
    // EFFECTS: sets current volume to input volume
    public void setVolume(float v) {
        FloatControl volumeControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
        if (v >= volumeControl.getMaximum()) {
            volumeControl.setValue(volumeControl.getMaximum());
        } else if (v <= volumeControl.getMinimum()) {
            volumeControl.setValue(volumeControl.getMinimum());
        } else {
            volumeControl.setValue(v);
        }
    }

    // MODIFIES: this
    // EFFECTS: mutes the sound.
    public void mute() {
        ((BooleanControl) soundClip.getControl(BooleanControl.Type.MUTE)).setValue(true);
    }

    // MODIFIES: this
    // EFFECTS: unmutes the sound.
    public void unmute() {
        ((BooleanControl) soundClip.getControl(BooleanControl.Type.MUTE)).setValue(false);
    }

    // EFFECTS: determines whether sound clip is muted (for testing purposes)
    public boolean isMuted() {
        return ((BooleanControl) soundClip.getControl(BooleanControl.Type.MUTE)).getValue();
    }

    // EFFECTS: returns max volume allowed by Java
    public float getMaxVolume() {
        return ((FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN)).getMaximum();
    }

    // EFFECTS: returns max volume allowed by Java
    public float getMinVolume() {
        return ((FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN)).getMinimum();
    }

    // MODIFIES: this
    // EFFECTS: when the sound file is playing, it will forever loop
    public void loop() {
        looping = true;
    }

    // MODIFIES: this
    // EFFECTS: stops looping (but if it is playing, it will play until completion)
    public void noLoop() {
        looping = false;
        // if it's running, make it not loop
        if (soundClip.isRunning()) {
            soundClip.loop(0);
        }
    }

    // EFFECTS: returns how long the sound file is in microseconds
    public long length() {
        return soundClip.getMicrosecondLength();
    }

    // EFFECTS: returns current position in time (when paused)
    public long getPausedTimePosition() {
        return timePosition;
    }

    // EFFECTS: returns current time position (when not paused)
    public long getTimePosition() {
        return soundClip.getMicrosecondPosition();
    }

    // MODIFIES: this
    // EFFECTS: pauses the file - another call to play will start playback from previous position
    public void pause() {
        timePosition = soundClip.getMicrosecondPosition();
        isPaused = true;
        soundClip.stop();
    }
}
