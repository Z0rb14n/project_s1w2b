package ui;

import model.SoundFile;
import ui.button.LoopButton;
import ui.button.MuteButton;
import ui.button.PausePlayButton;
import ui.button.StopButton;
import ui.slider.TimeSlider;
import ui.slider.VolumeSlider;

import javax.swing.*;
import java.awt.*;

// Wrapper class for all elements on bottom of panel (i.e. play/pause, sliders)
public class BottomAppPanel extends JPanel {
    private PausePlayButton ppb;
    private MuteButton mb;
    private StopButton sb;
    private JLabel timeStamp;
    private TimeSlider ts;
    private VolumeSlider vs;
    public static final int DEFAULT_WIDTH = AppPanel.DEFAULT_WIDTH;
    public static final int DEFAULT_HEIGHT = 40;
    public static final Color DEFAULT_BACKGROUND = new Color(255, 255, 255);

    // EFFECTS: initializes AppPanel with starting conditions
    BottomAppPanel() {
        ts = new TimeSlider();
        add(new LoopButton());
        ppb = new PausePlayButton();
        sb = new StopButton();
        mb = new MuteButton();
        vs = new VolumeSlider();
        timeStamp = new JLabel("0:00");
        add(ppb);
        add(sb);
        add(ts);
        add(timeStamp);
        add(mb);
        add(vs);
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setBackground(DEFAULT_BACKGROUND);
    }

    // MODIFIES: this
    // EFFECTS: updates all sub-components
    public void update() {
        ppb.update();
        sb.update();
        mb.update();
        ts.update();
        updateTimeStamp();
    }

    // MODIFIES: this
    // EFFECTS: updates time stamp to match current time
    private void updateTimeStamp() {
        timeStamp.setText(SoundFile.convertMicrosecondsToTimeStamp(ts.getDisplayedTime()));
    }
}
