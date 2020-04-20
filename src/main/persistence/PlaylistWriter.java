package persistence;

import model.Songs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

// Convenience class for writing playlists to file
// note that playlists are not supposed to save volume/mute/loop data
public class PlaylistWriter {
    public static final boolean SAVE_EXTENDED = false;
    public static final String DEFAULT_FILE_DIR = "./data/";
    public static final FileType DEFAULT_FILETYPE = FileType.EXTM3U;

    // EFFECTS: initializes a PlaylistWriter (although unnecessary)
    public PlaylistWriter() {
        // does nothing
    }

    // EFFECTS: saves the songs to DEFAULT_FILE_DIR/currentdateandtime.default_file_type
    //          throws IOException if file cannot be written to
    //          returns file name written to
    public String write(Songs s) throws IOException {
        String location = addFileExtension((new Date()).toString(), DEFAULT_FILETYPE);
        write(s, location, DEFAULT_FILETYPE, true);
        return DEFAULT_FILE_DIR + location;
    }

    // EFFECTS: saves the songs to DEFAULT_FILE_DIR/givenfile.default_file_type
    //          throws IOException if file cannot be written to
    public void write(Songs s, String file) throws IOException {
        FileType type = getFileExtension(file);
        if (type != null) {
            write(s, file, type, true);
        } else {
            write(s, file, DEFAULT_FILETYPE, true);
        }
    }

    // EFFECTS: saves the songs to DEFAULT_FILE_DIR/givenfile.fileType
    //          throws IllegalArgumentException if given file length is zero or file extensions do not match
    //          throws IOException if file cannot be written to
    public void write(Songs s, String file, FileType t) throws IOException {
        write(s, file, t, true);
    }

    // EFFECTS: saves the songs to givenfile.fileType, in either root or default directory according to savedef
    //          throws IllegalArgumentException if given file length is zero or file extensions do not match
    //          throws IOException if file cannot be written to
    public void write(Songs s, String file, FileType t, boolean savedef) throws IOException {
        checkValidFileExtension(file, t);
        String location;
        if (savedef) {
            if (getFileExtension(file) == null) {
                location = DEFAULT_FILE_DIR + addFileExtension(file, t);
            } else {
                location = DEFAULT_FILE_DIR + file;
            }
        } else {
            if (getFileExtension(file) == null) {
                location = addFileExtension(file, t);
            } else {
                location = file;
            }
        }
        save(s, location, t);
    }

    // EFFECTS: gets the file type of a file
    public static FileType getFileExtension(String file) {
        if (file.endsWith(".m3u") || file.endsWith(".m3u8")) {
            return FileType.EXTM3U;
        } else if (file.endsWith(".pls")) {
            return FileType.PLS;
        } else {
            return null;
        }
    }

    // EFFECTS: checks if the file path in string matches the file type t
    //          throws IllegalArgumentException if it does not match, or string/file length is 0
    private static void checkValidFileExtension(String s, FileType t) {
        if (s.length() == 0) {
            throw new IllegalArgumentException("Zero-length string in checkValidFileExtension.");
        }
        if ((s.endsWith(".m3u") || s.endsWith(".m3u8")) && t != FileType.EXTM3U) {
            throw new IllegalArgumentException("Invalid extension in file " + s + " when it should be .m3u or .m3u8");
        }
        if ((s.endsWith(".m3u") || s.endsWith(".pls")) && s.length() == 4) {
            throw new IllegalArgumentException("Zero-length file in checkValidFileExtension: " + s);
        }
        if (s.endsWith(".m3u8") && s.length() == 5) {
            throw new IllegalArgumentException("Zero-length file in checkValidFileExtension: " + s);
        }
        if (s.endsWith(".pls") && t != FileType.PLS) {
            throw new IllegalArgumentException("Invalid extension in file " + s + " when it should be .pls");
        }
    }

    // EFFECTS: adds the file extension of given type if does not exist
    //          throws IllegalArgumentException if file length is 0
    private static String addFileExtension(String s, FileType t) {
        checkValidFileExtension(s, t);
        if (getFileExtension(s) != null) {
            return s;
        }
        if (t == FileType.EXTM3U) {
            System.err.println("File extension not added: " + s + ". Adding .m3u");
            return s + ".m3u";
        } else {
            System.err.println("File extension not added: " + s + ". Adding .pls");
            return s + ".pls";
        }
    }

    // EFFECTS: saves a Songs instance to File.
    //          throws IllegalArgumentException if file length is zero, or file extension does not match file type
    //          throws IOException if file cannot be written to
    private static void save(Songs s, String file, FileType type) throws IOException {
        String location = addFileExtension(file, type);
        if (type == FileType.EXTM3U) {
            saveM3U(s, location);
        } else {
            savePLS(s, location);
        }
    }

    // EFFECTS: saves a file to file.m3u, and will add the .m3u extension if it does not exist
    //          throws IOException if file cannot be written to
    private static void saveM3U(Songs s, String file) throws IOException {
        File savedFile = new File(file);
        if (savedFile.exists()) {
            System.err.println("WARNING: File " + file + " exists. Overwriting contents...");
        }
        FileWriter writer = new FileWriter(savedFile);
        if (!SAVE_EXTENDED) {
            for (int i = 0; i < s.getLength(); i++) {
                writer.write(s.get(i).getString() + "\n");
            }
        } else {
            writer.write("#EXTM3U\n");
            for (int i = 0; i < s.getLength(); i++) {
                writer.write(s.get(i).getString() + "\n");
            }
        }
        writer.flush();
        writer.close();
    }

    // EFFECTS: saves a file to filename.pls, and will add the .pls if does not exist
    //          throws IOException if file cannot be written to
    private static void savePLS(Songs s, String file) throws IOException {
        File savedFile = new File(file);
        if (savedFile.exists()) {
            System.err.println("WARNING: File " + file + " exists. Overwriting contents...");
        }
        FileWriter writer = new FileWriter(savedFile);
        writer.write("[playlist]\n\n");
        for (int i = 0; i < s.getLength(); i++) {
            writer.write("File" + (i + 1) + "=" + s.get(i).getString() + "\n");
        }
        writer.write("NumberOfEntries=" + s.getLength() + "\n");
        writer.write("Version=2");
        writer.flush();
        writer.close();
    }
}
