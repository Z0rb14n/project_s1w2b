package ui;

import javax.swing.*;

// Wrapper with App utilizing JFrame
public class AppFrame extends JFrame {
    public static final int UPDATE_RATE = 20;
    private static final String TITLE = "\"l m a  o\"";
    private Timer updateTimer;
    private AppPanel ap;

    // EFFECTS: initializes a JFrame container for App
    AppFrame() {
        super(TITLE);
        ap = new AppPanel();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(false);
        setVisible(true);
        add(ap);
        pack();
        addTimer();
    }

    // MODIFIES: this
    // EFFECTS: creates the update timer
    public void addTimer() {
        updateTimer = new Timer(UPDATE_RATE, ae -> ap.update());
        updateTimer.start();
    }
}
