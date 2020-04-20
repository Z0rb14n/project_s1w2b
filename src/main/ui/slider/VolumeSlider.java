package ui.slider;

import ui.App;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// Represents the volume slider in the menu bar
public final class VolumeSlider extends Slider {
    private static final float MIN = -80;
    private static final float MAX = (float) 6.0206;
    private static final int START_VALUE = 930; // approximately +0 dB
    private static final int LENGTH = 175;

    // EFFECTS: initializes the volume sldier with default resolution
    public VolumeSlider() {
        super(LENGTH);
        setValue(START_VALUE);
        addChangeListener(new VolumeSliderListener());
    }

    // EFFECTS: linearly interpolates given float volume into an int value to show on a slider
    private static float interpVolume(int vol) {
        float ratio = ((float) vol) / RESOLUTION;
        float addedToMin = ratio * (MAX - MIN);
        return MIN + addedToMin;
    }

    // Represents a ChangeListener that changes the time only when the mouse stops moving
    private class VolumeSliderListener implements ChangeListener {

        // EFFECTS: Initializes default variables
        public VolumeSliderListener() {
        }

        @Override
        // MODIFIES: source object, App
        // EFFECTS: adjusts volume of application when mouse is moved
        public void stateChanged(ChangeEvent e) {
            VolumeSlider instanceSource = (VolumeSlider) e.getSource();
            if (!instanceSource.getValueIsAdjusting()) {
                int currentValue = getValue();
                App.getActiveApp().volume(interpVolume(currentValue));
            }
        }
    }
}
