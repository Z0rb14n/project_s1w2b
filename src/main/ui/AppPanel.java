package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// Represents the panel of all the parts in the App
public class AppPanel extends JPanel {
    public static final int DEFAULT_WIDTH = 1000;
    public static final int DEFAULT_HEIGHT = 480;
    public static final Color DEFAULT_BACKGROUND = new Color(255, 255, 255);
    public static final String CAT_IMG = "./data/icon/cat.png";
    public static BufferedImage catImage;

    private PlaylistView pv;
    private TopAppPanel tap;
    private BottomAppPanel bap;

    // EFFECTS: initializes AppPanel with starting conditions
    AppPanel() {
        initCat();
        setLayout(new BorderLayout());
        bap = new BottomAppPanel();
        add(bap, BorderLayout.PAGE_END);
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setBackground(DEFAULT_BACKGROUND);
        tap = new TopAppPanel();
        add(tap, BorderLayout.PAGE_START);
        pv = new PlaylistView();
        add(pv, BorderLayout.CENTER);
    }

    // MODIFIES: this
    // EFFECTS: initializes cat image (a "secret")
    private void initCat() {
        try {
            catImage = ImageIO.read(new File(CAT_IMG));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // MODIFIES: this
    // EFFECTS: updates and re-renders everything
    public void update() {
        pv.update();
        tap.update();
        bap.update();
        repaint();
    }

    @Override
    // MODIFIES: this
    // EFFECTS: re-draws the graphics of sub-components and allows drawing over
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        // uncomment for lulz
        //g.drawImage(catImage,0,0, catImage.getWidth()/4, catImage.getHeight()/4, null);
    }

    @Override
    // EFFECTS: returns the size of the panel
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
