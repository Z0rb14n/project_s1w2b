package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SongsTest {
    private Songs songs;
    public static final int DELAY = 300;
    public static final String file1 = "./data/440Hz.wav";
    public static final String file2 = "./data/sharpsAndFlats.wav";
    public static final String file3 = "./data/cmajor.wav";
    public static final String file4 = "./data/shouldNotWork.ogg";
    public static final float ERROR = (float) 0.1;

    @BeforeEach
    void runBefore() {
        songs = new Songs();
    }

    @AfterEach
    void runAfter() {
        songs.disableLoopPlaylist();
        songs.noLoop();
        songs.stopAll();
    }

    @Test
    void testJumpTo() {
        assertEquals(Songs.SKIPSTATUS_NOTPLAYING, songs.jumpTo(300));
        songs.add(file1);
        songs.add(file2);
        assertEquals(Songs.SKIPSTATUS_SUCCESSFUL, songs.jumpTo(100));
        delay(DELAY);
        assertEquals(0, songs.indexPlaying());
        assertEquals(Songs.SKIPSTATUS_SUCCESSFUL, songs.jumpTo(500));
        delay(DELAY);
        assertTrue(songs.get(0).getTimePosition() > 500);
        assertEquals(0, songs.indexPlaying());
        delay(DELAY);
        assertEquals(Songs.SKIPSTATUS_SUCCESSFUL, songs.jumpTo(100));
        assertEquals(0, songs.indexPlaying());
    }

    @Test
    void testMute() {
        songs.add(file1);
        songs.add(file2);
        songs.mute();
        for (int i = 0; i < songs.getLength(); i++) {
            assertTrue(songs.get(i).isMuted());
        }
        songs.add(file3);
        assertTrue(songs.get(2).isMuted());
        songs.unmute();
        for (int i = 0; i < songs.getLength(); i++) {
            assertFalse(songs.get(i).isMuted());
        }
    }

    @Test
    void testPlayNext() {
        songs.playNext();
        assertEquals(-1, songs.indexPlaying());
        songs.add(file1);
        songs.playNext();
        delay(DELAY);
        assertEquals(0, songs.indexPlaying());
        assertEquals(0, songs.getLastIndexPlayed());
        songs.add(file2);
        songs.add(file3);
        delay(DELAY);
        songs.playNext();
        delay(DELAY);
        assertEquals(1, songs.indexPlaying());
        delay(DELAY);
        songs.playNext();
        delay(DELAY);
        assertEquals(2, songs.indexPlaying());
        songs.playNext();
        assertEquals(0, songs.indexPlaying());
    }

    @Test
    void testLoopPlaylist() {
        songs.add(file1);
        songs.add(file2);
        songs.add(file3);
        songs.loopPlaylist();
        assertTrue(songs.isPlaylistLooping());
        delay(DELAY);
        assertEquals(0, songs.indexPlaying());
        delay((long) (songs.get(0).length() / 1000.0) + DELAY);
        assertEquals(1, songs.indexPlaying());
        delay((long) (songs.get(1).length() / 1000.0) + DELAY);
        assertEquals(2, songs.indexPlaying());
        delay((long) (songs.get(2).length() / 1000.0) + DELAY);
        assertEquals(0, songs.indexPlaying());
        delay((long) (songs.get(0).length() / 1000.0) + DELAY);
        assertEquals(1, songs.indexPlaying());
        songs.disableLoopPlaylist();
        assertFalse(songs.isPlaylistLooping());
    }

    @Test
    void testLoopEmptyPlaylist() {
        songs.loopPlaylist();
        assertTrue(songs.isPlaylistLooping());
        assertEquals(-1, songs.indexPlaying());
        songs.add(file1);
        delay(DELAY);
        assertEquals(0, songs.indexPlaying());
        delay((long) (Math.floor(file1.length() / 1000.0) + DELAY));
        assertTrue(songs.isPlaylistLooping());
        assertTrue(songs.get(0).isPlaying());
        assertEquals(0, songs.indexPlaying());
        songs.remove(file1);
        songs.disableLoopPlaylist();
        try {
            songs.disableLoopPlaylist();
        } catch (NullPointerException e) {
            fail();
        }
        assertFalse(songs.isPlaylistLooping());
    }

    @Test
    void testLoopPlaylistNonZeroStart() {
        songs.add(file1);
        songs.add(file2);
        songs.add(file3);
        songs.play(file3);
        songs.loopPlaylist();
        assertTrue(songs.isPlaylistLooping());
        delay(DELAY);
        assertEquals(2, songs.indexPlaying());
        delay((long) (songs.get(2).length() / 1000.0) + DELAY);
        assertEquals(0, songs.indexPlaying());
        delay((long) (songs.get(0).length() / 1000.0) + DELAY);
        assertEquals(1, songs.indexPlaying());
        delay((long) (songs.get(1).length() / 1000.0) + DELAY);
        assertEquals(2, songs.indexPlaying());
        songs.disableLoopPlaylist();
        assertFalse(songs.isPlaylistLooping());
    }

    // EFFECTS: delays execution by a set number of milliseconds
    void delay(long amount) {
        try {
            Thread.sleep(amount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testInit() {
        assertEquals(0, songs.getLength());
        assertFalse(songs.isPlaylistLooping());
        assertFalse(songs.isSingleSongLooping());
        assertTrue(songs.isEmpty());
        assertEquals("", songs.list());
        assertEquals(Songs.PLAYSTATUS_NOTFOUND, songs.play(file1));
        assertEquals(Songs.PLAYSTATUS_NOTFOUND, songs.play("rick astley"));
        assertEquals(Songs.PLAYSTATUS_NOTFOUND, songs.play("all star"));
        assertFalse(songs.remove("somebody once told me the world was gonna roll me"));
    }

    @Test
    void testAdd() {
        songs.add(file1);
        assertEquals(Songs.PLAYSTATUS_SUCCESSFUL, songs.play(file1));
        assertEquals(Songs.PLAYSTATUS_SUCCESSFUL, songs.play(file1));
        assertEquals(file1, songs.getString(0));
        assertEquals(file1, songs.get(0).getFile().getPath());
        assertEquals(1, songs.getLength());
    }

    @Test
    void testList() {
        testAdd();
        songs.add(file2);
        assertEquals(file1 + "\n" + file2, songs.list());
    }

    @Test
    void testVolume() {
        testList();
        float value = (float) 2.0;
        songs.setVolume(value);
        assertTrue(Math.abs(songs.getVolume() - value) < ERROR);
        for (int i = 0; i < songs.getLength(); i++) {
            assertTrue(Math.abs(songs.get(i).getVolume() - value) < ERROR);
        }
    }

    @Test
    void testVolumeBoundary() {
        testList();
        float value = (float) 72.0;
        float max = (float) 6.0206;
        float min = (float) -80;
        songs.setVolume(value);
        assertFalse(Math.abs(songs.getVolume() - value) < ERROR);
        for (int i = 0; i < songs.getLength(); i++) {
            assertFalse(Math.abs(songs.get(i).getVolume() - value) < ERROR);
            assertTrue(Math.abs(songs.get(i).getVolume() - max) < ERROR);
        }
        value = min - 10;
        songs.setVolume(min - 10);
        assertFalse(Math.abs(songs.getVolume() - value) < ERROR);
        for (int i = 0; i < songs.getLength(); i++) {
            assertFalse(Math.abs(songs.get(i).getVolume() - value) < ERROR);
            assertTrue(Math.abs(songs.get(i).getVolume() - min) < ERROR);
        }
    }

    @Test
    void testIndexOf() {
        songs.add(file1);
        songs.add(file2);
        songs.add(file3);
        songs.play(file2);
        delay(DELAY);
        assertEquals(1, songs.indexPlaying());
        delay((long) Math.floor(songs.get(1).length() / 1000.0));
        assertEquals(-1, songs.indexPlaying());
    }

    @Test
    void testLoop() {
        testList();
        songs.loop();
        assertTrue(songs.isSingleSongLooping());
        for (int i = 0; i < songs.getLength(); i++) {
            assertTrue(songs.get(i).isLooping());
        }
        songs.noLoop();
        assertFalse(songs.isSingleSongLooping());
        for (int i = 0; i < songs.getLength(); i++) {
            assertFalse(songs.get(i).isLooping());
        }
    }

    @Test
    void testPause() {
        assertFalse(songs.isPaused());
        assertEquals(Songs.ADDSTATUS_SUCCESSFUL, songs.add(file3));
        songs.stopAll();
        assertEquals(Songs.PLAYSTATUS_SUCCESSFUL, songs.play(file3));
        assertFalse(songs.isPaused());
        delay(DELAY);
        assertTrue(songs.pause());
        assertTrue(songs.isPaused());
        assertFalse(songs.get(0).isPlaying());
        assertTrue(songs.get(0).isPaused());
        assertTrue(songs.get(0).getPausedTimePosition() >= DELAY);
        assertEquals(Songs.PLAYSTATUS_SUCCESSFUL, songs.play(file3));
        delay(DELAY);
        assertTrue(songs.get(0).isPlaying());
        assertFalse(songs.get(0).isPaused());
        assertFalse(songs.isPaused());
        assertEquals(0, songs.get(0).getPausedTimePosition());
    }

    @Test
    void testGetCurrentPosition() {
        songs.add(file1);
        songs.add(file2);
        songs.add(file3);
        songs.play(file3);
        delay(DELAY);
        assertTrue(songs.getCurrentPosition() >= DELAY * 500);
        songs.stopAll();
        songs.play(file1);
        delay(DELAY);
        assertTrue(songs.getCurrentPosition() >= DELAY * 500);
        songs.stopAll();
        assertEquals(-1, songs.getCurrentPosition());
    }

    @Test
    void testIterator() {
        assertFalse(songs.iterator().hasNext());
        songs.add(file1);
        songs.add(file2);
        songs.add(file3);
        assertTrue(songs.iterator().hasNext());
        assertEquals(file1, songs.iterator().next().getString());
        int i = 0;
        for (SoundFile sf : songs) {
            if (i == 0) {
                assertEquals(file1, sf.getString());
            } else if (i == 1) {
                assertEquals(file2, sf.getString());
            } else if (i == 2) {
                assertEquals(file3, sf.getString());
            }
            i++;
        }
    }

    @Test
    void testMultiplePause() {
        int desired = Songs.ADDSTATUS_SUCCESSFUL;
        assertEquals(desired, songs.add(file1));
        assertEquals(desired, songs.add(file2));
        assertEquals(desired, songs.add(file3));
        songs.stopAll();
        assertEquals(Songs.PLAYSTATUS_SUCCESSFUL, songs.play(file2));
        delay(DELAY);
        assertTrue(songs.pause());
        assertFalse(songs.get(1).isPlaying());
        assertTrue(songs.get(1).isPaused());
        assertTrue(songs.get(1).getPausedTimePosition() >= DELAY);
        songs.play(file2);
        delay(DELAY);
        assertTrue(songs.get(1).isPlaying());
        assertFalse(songs.get(1).isPaused());
        assertEquals(0, songs.get(1).getPausedTimePosition());
        songs.stopAll();
        assertFalse(songs.pause());
    }

    @Test
    void testBadAdd() {
        assertEquals(Songs.ADDSTATUS_UNSUPPORTED, songs.add(file4));
        assertEquals(0,songs.getLength());
        assertEquals(Songs.ADDSTATUS_ERROR, songs.add("big boi brain"));
        assertEquals(Songs.PLAYSTATUS_NOTFOUND, songs.play("big boi brain"));
        assertEquals(0, songs.getLength());
        assertEquals(Songs.ADDSTATUS_SUCCESSFUL, songs.add(file1));
        assertEquals(Songs.ADDSTATUS_EXISTS, songs.add(file1));
    }
    @Test
    void testMultiple() {
        assertEquals(Songs.ADDSTATUS_SUCCESSFUL, songs.add(file1));
        assertEquals(Songs.PLAYSTATUS_SUCCESSFUL, songs.play(file1));
        assertEquals(Songs.PLAYSTATUS_SUCCESSFUL, songs.play(file1));
        assertEquals(1, songs.getLength());
        assertEquals(file1, songs.getString(0));
        assertEquals(Songs.ADDSTATUS_SUCCESSFUL, songs.add(file2));
        assertEquals(Songs.ADDSTATUS_SUCCESSFUL, songs.add(file3));
        assertEquals(Songs.PLAYSTATUS_SUCCESSFUL, songs.play(file2));
        songs.play(file3);
        delay(DELAY);
        assertFalse(songs.get(1).isPlaying());
        assertFalse(songs.get(0).isPlaying());
        assertEquals(3, songs.getLength());
        assertEquals(file2, songs.getString(1));
    }

    @Test
    void testClear() {
        testMultiple();
        songs.clear();
        assertEquals(Songs.PLAYSTATUS_NOTFOUND, songs.play(file1));
        try {
            assertFalse(songs.get(0).isPlaying());
            Assertions.fail();
        } catch (IndexOutOfBoundsException e) {
            // since size == 0, this should happen
        }
        assertEquals(0, songs.getLength());
    }

    @Test
    void testRemoveMany() {
        testMultiple();
        assertTrue(songs.remove(file2));
        assertEquals(Songs.PLAYSTATUS_NOTFOUND, songs.play(file2));
        assertEquals(2, songs.getLength());
        assertTrue(songs.remove(file1));
        assertEquals(Songs.PLAYSTATUS_NOTFOUND, songs.play(file1));
        assertEquals(1, songs.getLength());
        assertTrue(songs.remove(file3));
        assertEquals(Songs.PLAYSTATUS_NOTFOUND, songs.play(file3));
        assertEquals(0, songs.getLength());
        try {
            assertFalse(songs.get(0).isPlaying());
            Assertions.fail();
        } catch (IndexOutOfBoundsException e) {
            // it's supposed to happen, since size == 0
        }
    }
}
