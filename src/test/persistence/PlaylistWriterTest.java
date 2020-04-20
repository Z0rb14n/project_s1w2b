package persistence;

import exception.ParsingException;
import model.Songs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

// Test class for PlaylistWriter class
public class PlaylistWriterTest {
    PlaylistWriter pw = new PlaylistWriter();
    PlaylistParser pp = new PlaylistParser();
    Songs s = new Songs();
    public static final String DEFAULT_ADDED_EXTENSION = ".m3u"; // changed by programmer manually
    public static final String SONG1 = "./data/sharpsAndFlats.wav";
    public static final String SONG2 = "./data/cmajor.wav";

    @BeforeEach
    void runBefore() {
        pw = new PlaylistWriter();
        pp = new PlaylistParser();
        s = new Songs();
        s.add(SONG1);
        s.add(SONG2);
    }

    @Test
    void testEmpty() {
        s = new Songs();
        try {
            String location = pw.write(s);
            Songs s1 = pp.getSongs(location);
            assertEquals(0, s1.getLength());
        } catch (IOException | ParsingException e) {
            fail();
        }
    }

    @Test
    void testFileNoFileTypeConstructor() {
        try {
            pw.write(s, "not pirated");
            String actualLocation = PlaylistWriter.DEFAULT_FILE_DIR + "not pirated" + DEFAULT_ADDED_EXTENSION;
            Songs s1 = pp.getSongs(actualLocation);
            assertEquals(2, s1.getLength());
            assertEquals(SONG1, s1.get(0).getString());
            assertEquals(SONG2, s1.get(1).getString());
        } catch (IOException | ParsingException e) {
            fail();
        }
    }

    @Test
    void testFileGivenFileTypeConstructor() {
        try {
            pw.write(s, "not pirated.m3u");
            String actualLocation = PlaylistWriter.DEFAULT_FILE_DIR + "not pirated.m3u";
            Songs s1 = pp.getSongs(actualLocation);
            assertEquals(2, s1.getLength());
            assertEquals(SONG1, s1.get(0).getString());
            assertEquals(SONG2, s1.get(1).getString());
        } catch (IOException | ParsingException e) {
            fail();
        }
    }

    @Test
    void testFileGivenFileTypeConstructorPLS() {
        try {
            pw.write(s, "not pirated.pls");
            String actualLocation = PlaylistWriter.DEFAULT_FILE_DIR + "not pirated.pls";
            Songs s1 = pp.getSongs(actualLocation);
            assertEquals(2, s1.getLength());
            assertEquals(SONG1, s1.get(0).getString());
            assertEquals(SONG2, s1.get(1).getString());
        } catch (IOException | ParsingException e) {
            fail();
        }
    }

    @Test
    void testFileGivenFileTypeConstructorM3U8() {
        try {
            pw.write(s, "not pirated.m3u8");
            String actualLocation = PlaylistWriter.DEFAULT_FILE_DIR + "not pirated.m3u8";
            Songs s1 = pp.getSongs(actualLocation);
            assertEquals(2, s1.getLength());
            assertEquals(SONG1, s1.get(0).getString());
            assertEquals(SONG2, s1.get(1).getString());
        } catch (IOException | ParsingException e) {
            fail();
        }
    }

    @Test
    void testOverwrite() {
        try {
            pw.write(s, "not pirated");
            String actualLocation = PlaylistWriter.DEFAULT_FILE_DIR + "not pirated" + DEFAULT_ADDED_EXTENSION;
            Songs s1 = pp.getSongs(actualLocation);
            assertEquals(2, s1.getLength());
            assertEquals(SONG1, s1.get(0).getString());
            assertEquals(SONG2, s1.get(1).getString());
            pw.write(s, "not pirated");
            actualLocation = PlaylistWriter.DEFAULT_FILE_DIR + "not pirated" + DEFAULT_ADDED_EXTENSION;
            s1 = pp.getSongs(actualLocation);
            assertEquals(2, s1.getLength());
            assertEquals(SONG1, s1.get(0).getString());
            assertEquals(SONG2, s1.get(1).getString());
        } catch (IOException | ParsingException e) {
            fail();
        }
    }

    @Test
    void testZeroLengthFile() {
        try {
            pw.write(s, "");
            fail();
        } catch (IOException | ParsingException e) {
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    void testZeroLengthM3U() {
        try {
            pw.write(s, ".m3u");
            fail();
        } catch (IOException | ParsingException e) {
            fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            pw.write(s, ".m3u8");
            fail();
        } catch (IOException | ParsingException e) {
            fail();
        } catch (IllegalArgumentException e) {
        }
    }


    @Test
    void testZeroLengthPLS() {
        try {
            pw.write(s, ".pls");
            fail();
        } catch (IOException | ParsingException e) {
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    void testInvalidExtensions() {
        try {
            pw.write(s, "hax.m3u", FileType.PLS);
            fail();
        } catch (IOException | ParsingException e) {
            fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            pw.write(s, "hax.pls", FileType.EXTM3U);
            fail();
        } catch (IOException | ParsingException e) {
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    void testFileWithFileTypeConstructor() {
        try {
            pw.write(s, "not pirated", FileType.EXTM3U);
            String actualLocation = PlaylistWriter.DEFAULT_FILE_DIR + "not pirated" + ".m3u";
            Songs s1 = pp.getSongs(actualLocation);
            assertEquals(2, s1.getLength());
            assertEquals(SONG1, s1.get(0).getString());
            assertEquals(SONG2, s1.get(1).getString());
            pw.write(s, "not pirated", FileType.PLS);
            actualLocation = PlaylistWriter.DEFAULT_FILE_DIR + "not pirated" + ".pls";
            s1 = pp.getSongs(actualLocation);
            assertEquals(2, s1.getLength());
            assertEquals(SONG1, s1.get(0).getString());
            assertEquals(SONG2, s1.get(1).getString());
        } catch (IOException | ParsingException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testFileWithSaveDefaultConstructor() {
        try {
            pw.write(s, "./data/not pirated", FileType.EXTM3U, false);
            String actualLocation = PlaylistWriter.DEFAULT_FILE_DIR + "not pirated.m3u";
            Songs s1 = pp.getSongs(actualLocation);
            assertEquals(2, s1.getLength());
            assertEquals(SONG1, s1.get(0).getString());
            assertEquals(SONG2, s1.get(1).getString());
            pw.write(s, "./data/not pirated", FileType.PLS, false);
            actualLocation = PlaylistWriter.DEFAULT_FILE_DIR + "not pirated.pls";
            s1 = pp.getSongs(actualLocation);
            assertEquals(2, s1.getLength());
            assertEquals(SONG1, s1.get(0).getString());
            assertEquals(SONG2, s1.get(1).getString());
        } catch (IOException | ParsingException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testFileWithAlreadyAddedExtensions() {
        try {
            String location = PlaylistWriter.DEFAULT_FILE_DIR + "not pirated.m3u";
            String location2 = PlaylistWriter.DEFAULT_FILE_DIR + "not pirated.pls";
            pw.write(s, "not pirated.m3u", FileType.EXTM3U);
            Songs s1 = pp.getSongs(location);
            assertEquals(2, s1.getLength());
            assertEquals(SONG1, s1.get(0).getString());
            assertEquals(SONG2, s1.get(1).getString());
            pw.write(s, "not pirated.pls", FileType.PLS);
            s1 = pp.getSongs(location2);
            assertEquals(2, s1.getLength());
            assertEquals(SONG1, s1.get(0).getString());
            assertEquals(SONG2, s1.get(1).getString());
        } catch (IOException | ParsingException e) {
            fail();
        }
    }

    @Test
    void testFileWithExtensionsAlreadyAddedLongConstructor() {
        try {
            String location = PlaylistWriter.DEFAULT_FILE_DIR + "not pirated.m3u";
            String location2 = PlaylistWriter.DEFAULT_FILE_DIR + "not pirated.pls";
            pw.write(s, "./data/not pirated.pls", FileType.PLS, false);
            Songs s1 = pp.getSongs(location2);
            assertEquals(2, s1.getLength());
            assertEquals(SONG1, s1.get(0).getString());
            assertEquals(SONG2, s1.get(1).getString());
            pw.write(s, "./data/not pirated.m3u", FileType.EXTM3U, false);
            s1 = pp.getSongs(location);
            assertEquals(2, s1.getLength());
            assertEquals(SONG1, s1.get(0).getString());
            assertEquals(SONG2, s1.get(1).getString());
        } catch (IOException | ParsingException e) {
            fail();
        }
    }

    // EFFECTS: delays execution by ms milliseconds.
    void delay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }
}
