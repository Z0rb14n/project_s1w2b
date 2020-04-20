package persistence;

import exception.ParsingException;
import model.Songs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

// Test class for Playlist Parser class
public class PlaylistParserTest {
    PlaylistParser pp;

    public final static String INVALID_FILE_NUMBERS = "./data/invalidfilenum.pls";
    public final static String ENTIRELY_EMPTY_PLS = "./data/definitelynotpls.pls";
    public final static String INVALID_PLAYLIST = "lol mao.pls";
    public final static String EMPTY_M3U = "./data/emptym3u.m3u";
    public final static String EMPTY_PLS = "./data/emptypls.pls";
    public final static String NON_RECURSIVE_M3U = "./data/nonrecursivem3u.m3u";
    public final static String NON_RECURSIVE_M3U8 = "./data/nonrecursivem3u copy.m3u8";
    public final static String NON_RECURSIVE_PLS = "./data/nonrecursivepls.pls";
    public final static String RECURSIVE_PLS = "./data/recursive.pls";
    public final static String RECURSIVE_M3U = "./data/recursivem3u.m3u";
    public final static String INFINITE_RECURSIVE_M3U = "./data/infiniterecursivem3u.m3u";
    public final static String INFINITE_RECURSIVE_PLS = "./data/infiniterecursivepls.pls";
    public final static String RECURSIVE_M3U_PLS = "./data/recursivem3upls.m3u";
    public final static String BAD_M3U_FILEEX = "./data/badm3uextension.lol";
    public final static String BAD_PLS_FILEEX = "./data/badplsextension.lmao";
    public final static String BAD_ENTRY_NUM = "./data/badentrynum.pls";
    public final static String PLS_FILE_MISSING = "./data/filemissing.pls";
    public final static String PLS_WITH_EXTRAS = "./data/plswithextras.pls";
    public final static String BAD_PLS_ORDER = "./data/reversed.pls";
    public final static String EXT_M3U = "./data/EXTM3U.m3u";
    public final static String MISSING_FILES_M3U = "./data/missingfiles.m3u";
    public final static String PLS_WITH_UNSUPPORTED = "./data/unsupported.pls";

    @BeforeEach
    void runBefore() {
        pp = new PlaylistParser();
    }

    @Test
    void testInvalidFileNumbers() {
        try {
            Songs s = pp.getSongs(INVALID_FILE_NUMBERS);
            fail();
        } catch (ParsingException e) {
        } catch (FileNotFoundException e) {
            fail();
        }
    }

    @Test
    void testUnsupportedInternalFile() {
        try {
            Songs s = pp.getSongs(PLS_WITH_UNSUPPORTED);
            assertEquals(1, s.getLength());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testInternalFileNotFound() {
        try {
            Songs s = pp.getSongs(MISSING_FILES_M3U);
            assertEquals(2, s.getLength());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testInfiniteRecursion() {
        try {
            Songs s = pp.getSongs(INFINITE_RECURSIVE_M3U);
            assertEquals(3, s.getLength());
            Songs s2 = pp.getSongs(INFINITE_RECURSIVE_PLS);
            assertEquals(1, s2.getLength());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testExtraDataPLS() {
        try {
            Songs s = pp.getSongs(PLS_WITH_EXTRAS);
            assertEquals(2, s.getLength());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testEntirelyEmptyPLS() {
        try {
            Songs s = pp.getSongs(ENTIRELY_EMPTY_PLS);
            fail();
        } catch (ParsingException e) {
        } catch (FileNotFoundException e) {
            fail();
        }
    }

    @Test
    void testNonExistentPlaylist() {
        try {
            Songs s = pp.getSongs(INVALID_PLAYLIST);
            fail();
        } catch (FileNotFoundException e) {
        } catch (ParsingException e) {
            fail();
        }
    }

    @Test
    void testM3UComments() {
        try {
            Songs s = pp.getSongs(EXT_M3U);
            assertEquals(2, s.getLength());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testBadFormatting() {
        try {
            Songs s = pp.getSongs(BAD_PLS_ORDER);
            fail();
        } catch (ParsingException e) {
        } catch (FileNotFoundException e) {
            fail();
        }
    }

    @Test
    void testMissingFiles() {
        try {
            Songs s = pp.getSongs(PLS_FILE_MISSING);
            assertEquals(2, s.getLength());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testBadEntryNum() {
        try {
            Songs s = pp.getSongs(BAD_ENTRY_NUM);
            fail();
        } catch (ParsingException e) {
        } catch (FileNotFoundException e) {
            fail();
        }
    }

    @Test
    void testBadExtensions() {
        try {
            Songs s = pp.getSongs(BAD_M3U_FILEEX);
            fail();
        } catch (ParsingException e) {
        } catch (FileNotFoundException e) {
            fail();
        }
        try {
            Songs s = pp.getSongs(BAD_PLS_FILEEX);
            fail();
        } catch (ParsingException e) {
        } catch (FileNotFoundException e) {
            fail();
        }
    }

    @Test
    void testEmptyM3U() {
        try {
            Songs s = pp.getSongs(EMPTY_M3U);
            assertEquals(0, s.getLength());
        } catch (ParsingException | FileNotFoundException e) {
            fail();
        }
    }

    @Test
    void testEmptyPLS() {
        try {
            Songs s = pp.getSongs(EMPTY_PLS);
            assertEquals(0, s.getLength());
        } catch (ParsingException | FileNotFoundException e) {
            fail();
        }
    }

    @Test
    void testNonRecursivePLS() {
        try {
            Songs s = pp.getSongs(NON_RECURSIVE_PLS);
            assertEquals(2, s.getLength());
            assertEquals("./data/sharpsAndFlats.wav", s.get(0).getString());
            assertEquals("./data/cmajor.wav", s.get(1).getString());
        } catch (ParsingException | FileNotFoundException e) {
            fail();
        }
    }

    @Test
    void testNonRecursiveM3U() {
        try {
            Songs s = pp.getSongs(NON_RECURSIVE_M3U);
            assertEquals(2, s.getLength());
            assertEquals("./data/440Hz.wav", s.get(0).getString());
            assertEquals("./data/1000 Hz.wav", s.get(1).getString());
            Songs s2 = pp.getSongs(NON_RECURSIVE_M3U8);
            assertEquals(2, s2.getLength());
        } catch (ParsingException | FileNotFoundException e) {
            fail();
        }
    }

    @Test
    void testInfiniteRecursiveM3U() {
        try {
            Songs s = pp.getSongs(RECURSIVE_M3U);
            assertEquals(3, s.getLength());
            Songs s2 = pp.getSongs(RECURSIVE_PLS);
            assertEquals(2, s2.getLength());
            Songs s3 = pp.getSongs(RECURSIVE_M3U_PLS);
            assertEquals(3, s3.getLength());
        } catch (ParsingException | FileNotFoundException e) {
            fail();
        }
    }
}
