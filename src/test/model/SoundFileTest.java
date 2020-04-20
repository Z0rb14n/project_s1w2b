package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.UnsupportedAudioFileException;

import static org.junit.jupiter.api.Assertions.*;

// JUnit test for SoundFile class
public class SoundFileTest {
    public static final String DEBUG_FILE1 = "./data/cmajor.wav";
    public static final long LENGTH1 = 3200000; // empirically determined
    public static final String DEBUG_FILE2 = "./data/440Hz.wav";
    public static final long LENGTH2 = 1500000; // empirically determined
    public static final String DEBUG_FILE_LONG = "./data/OneMinuteA4.wav";
    public static final String BAD_DEBUG = "./data/shouldNotWork.ogg"; // unsupported sound file type
    public static final int DELAY = 500;
    public static final int LOOP_COUNT = 1;
    public static final float ERROR = (float) 0.1; // floating point errors
    private SoundFile sf;

    // EFFECTS: Delays the current thread by time milliseconds
    static void delay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace(); // should not happen
        }
    }

    @BeforeEach
    void runBefore() {
        try {
            sf = new SoundFile(DEBUG_FILE1);
        } catch (Exception e) {
            fail(); // should not happen
        }
    }

    @AfterEach
    void runAfter() {
        sf.stop();
        System.gc();
    }

    @Test
    void testJumpTo() {
        sf.play();
        sf.jumpTo(1000);
        delay(DELAY);
        assertTrue(sf.getTimePosition() > 1000);
    }

    @Test
    void testStringLength() {
        assertEquals("0:03", sf.stringLength());
        try {
            sf = new SoundFile(DEBUG_FILE_LONG);
            assertEquals("1:11", sf.stringLength());
        } catch (Exception e) {
            fail("u done goof bud");
        }
    }

    @Test
    void testInit() {
        assertEquals(DEBUG_FILE1, sf.getString());
        assertFalse(sf.isPlaying());
        assertEquals(DEBUG_FILE1, sf.getFile().getPath());
    }

    @Test
    void testMute() {
        assertFalse(sf.isMuted());
        sf.mute();
        assertTrue(sf.isMuted());
        sf.unmute();
        assertFalse(sf.isMuted());
    }

    @Test
    void testLength() {
        assertEquals(LENGTH1, sf.length());
        try {
            SoundFile sf2 = new SoundFile(DEBUG_FILE2);
            assertEquals(LENGTH2, sf2.length());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testLoop() {
        sf.loop();
        assertFalse(sf.isPlaying());
        assertTrue(sf.isLooping());
        sf.play();
        delay(DELAY);
        assertTrue(sf.isPlaying());
        assertTrue(sf.isLooping());
        delay((long) Math.floor(sf.length() / 1000.0) * LOOP_COUNT);
        assertTrue(sf.isPlaying());
        assertTrue(sf.isLooping());
        sf.noLoop();
        assertFalse(sf.isLooping());
        delay((long) Math.floor(sf.length() / 1000.0) + DELAY);
        assertFalse(sf.isPlaying());
    }

    @Test
    void testNoLoopNotPlaying() {
        sf.loop();
        assertFalse(sf.isPlaying());
        assertTrue(sf.isLooping());
        sf.noLoop();
        assertFalse(sf.isLooping());
        assertFalse(sf.isPlaying());
    }

    @Test
    void testPause() {
        sf.play();
        delay(DELAY);
        assertTrue(sf.isPlaying());
        assertFalse(sf.isPaused());
        assertEquals(0, sf.getPausedTimePosition());
        sf.pause();
        assertTrue(sf.getPausedTimePosition() >= DELAY);
        assertTrue(sf.isPaused());
        assertFalse(sf.isPlaying());
        sf.play();
        delay(DELAY);
        assertTrue(sf.isPlaying());
        assertFalse(sf.isPaused());
        assertEquals(0, sf.getPausedTimePosition());
    }

    @Test
    void testGetCurrentPosition() {
        sf.play();
        delay(DELAY);
        assertTrue(sf.getTimePosition() >= (DELAY * 500));
        // isn't 1000 since there's a short delay before it starts playing
    }

    @Test
    void testBadInit() {
        try {
            sf = new SoundFile("big brain plays");
            fail();
        } catch (IllegalArgumentException e) {
            // do nothing
        } catch (Exception e) {
            fail();
        }
        try {
            sf = new SoundFile(BAD_DEBUG);
            fail();
        } catch (UnsupportedAudioFileException e) {
            // do nothing
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testVolume() {
        sf.play();
        delay(DELAY);
        assertTrue(Math.abs(sf.getVolume()) < ERROR); // default volume is zero
        sf.setVolume((float) -30.0);
        delay(DELAY);
        assertTrue(Math.abs(sf.getVolume() + 30.0) < ERROR);
        sf.setVolume(sf.getMinVolume() - 69340);
        delay(DELAY);
        assertTrue(Math.abs(sf.getVolume() - sf.getMinVolume()) < ERROR);
        sf.setVolume(sf.getMaxVolume() + 30);
        delay(DELAY);
        assertTrue(Math.abs(sf.getVolume() - sf.getMaxVolume()) < ERROR);
    }

    @Test
    void testPlay() {
        try {
            sf = new SoundFile(DEBUG_FILE1);
        } catch (Exception e) {
            fail();
        }
        assertFalse(sf.isPlaying());
        sf.play();
        delay(DELAY);
        assertTrue(sf.isPlaying());
        sf.stop();
        assertFalse(sf.isPlaying());
    }

    @Test
    void testStop() {
        testInit();
        sf.play();
        delay(DELAY);
        assertTrue(sf.isPlaying());
        sf.stop();
        assertFalse(sf.isPlaying());
        assertEquals(DEBUG_FILE1, sf.getString());
    }
}
