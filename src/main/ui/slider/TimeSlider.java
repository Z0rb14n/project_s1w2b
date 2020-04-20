package ui.slider;

import ui.App;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// Represents the time slider in the menu bar
public final class TimeSlider extends Slider {
    private static final int LENGTH = 400;
    private int prevVal;

    // EFFECTS: initializes the TimeSlider with initial values
    public TimeSlider() {
        super(LENGTH);
        addChangeListener(new TimeSliderListener());
        prevVal = 0;
    }

    // EFFECTS: returns the currently displayed time
    public long getDisplayedTime() {
        return interpTime(prevVal);
    }

    // MODIFIES: this
    // EFFECTS: updates the time slider to match the actual position of the song
    public void update() {
        long currentPosition = App.getActiveApp().getCurrentMicrosecondPosition();
        int indexPlaying = App.getActiveApp().getCurrentIndexPlaying();
        int lastIndexPlayed = App.getActiveApp().getLastIndexPlayed();
        boolean isPaused = App.getActiveApp().isCurrentPlaylistPaused();
        boolean isEmpty = App.getActiveApp().isCurrentPlaylistEmpty();
        if (indexPlaying == -1) {
            if (!isEmpty && lastIndexPlayed != -1 && isPaused) {
                prevVal = interpPosition(App.getActiveApp().getLastPausedMicrosecondPosition());
                setValue(prevVal);
            } else {
                prevVal = 0;
                setValue(0);
            }
        } else {
            if (!getValueIsAdjusting()) {
                long totalLength = App.getActiveApp().getCurrentMicrosecondLength() + 1;
                long actualPosition = Math.floorMod(currentPosition, totalLength);
                int val = (int) Math.floorDiv(actualPosition * RESOLUTION, totalLength);
                prevVal = val;
                setValue(val);
            }
        }
    }

    // EFFECTS: linearly interpolates a time into a point on a slider
    //          throws IllegalArgumentException if current Songs instance is empty
    public int interpPosition(long time) {
        if (time == 0) {
            return 0;
        }
        int indexPlaying = App.getActiveApp().getCurrentIndexPlaying();
        int lastIndexPlayed = App.getActiveApp().getLastIndexPlayed();
        if (App.getActiveApp().isCurrentPlaylistEmpty() && indexPlaying == -1 && lastIndexPlayed == -1) {
            throw new IllegalArgumentException("Interpolated time on zero-length time instance.");
        }
        int currentlyPlaying;
        if (indexPlaying == -1) {
            currentlyPlaying = (lastIndexPlayed == -1) ? 0 : lastIndexPlayed;
        } else {
            currentlyPlaying = indexPlaying;
        }
        return (int) ((time / (float) App.getActiveApp().getMicrosecondLength(currentlyPlaying)) * RESOLUTION);
    }

    // EFFECTS: linearly interpolates the time as a point on a slider
    //          throws IllegalArgumentException if current Songs instance is empty
    public long interpTime(int pos) {
        if (pos == 0) {
            return 0;
        }
        int indexPlaying = App.getActiveApp().getCurrentIndexPlaying();
        int lastIndexPlayed = App.getActiveApp().getLastIndexPlayed();
        if (App.getActiveApp().isCurrentPlaylistEmpty() && indexPlaying == -1 && lastIndexPlayed == -1) {
            throw new IllegalArgumentException("Interpolated time on zero-length time instance.");
        }
        float ratio = ((float) pos) / RESOLUTION;
        int currentlyPlaying;
        if (indexPlaying == -1) {
            currentlyPlaying = (lastIndexPlayed == -1) ? 0 : lastIndexPlayed;
        } else {
            currentlyPlaying = indexPlaying;
        }
        return Math.round(ratio * App.getActiveApp().getMicrosecondLength(currentlyPlaying));
    }

    // Represents a ChangeListener that changes the time only when the mouse stops moving
    private class TimeSliderListener implements ChangeListener {
        public static final String MSG = "Cannot skip forward in empty songs instance.";
        public static final String HEADER = "TimeSlider: ERROR";

        // EFFECTS: initializes a blank TimeSliderListener
        public TimeSliderListener() {
        }

        @Override
        // MODIFIES: TimeSlider (the one the listener is attached to)
        // EFFECTS: Changes the time on the TimeSlider and in the App when the state is changed
        public void stateChanged(ChangeEvent e) {
            TimeSlider instanceSource = (TimeSlider) e.getSource();
            if (!instanceSource.getValueIsAdjusting()) {
                int currentValue = getValue();
                if (currentValue == prevVal) {
                    return;
                }
                try {
                    App.getActiveApp().jumpTo(interpTime(currentValue));
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(instanceSource, MSG, HEADER, JOptionPane.ERROR_MESSAGE);
                    setValue(0);
                }
            }
        }
    }
}
