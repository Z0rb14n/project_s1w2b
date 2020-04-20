package persistence;

import exception.ParsingException;
import model.Songs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

// Convenience class to read playlists from files
public class PlaylistParser {
    // file separator
    public static final String SEP = File.separator;

    // EFFECTS: initializes a playlist parser (although it just makes calls to static methods)
    public PlaylistParser() {
        // do absolutely nothing
    }

    // EFFECTS: returns a songs instance corresponding to the playlist
    //          throws ParsingError if it cannot be read
    //          throws FileNotFoundException if file... is  not found
    public Songs getSongs(String file) throws FileNotFoundException {
        return read(file);
    }

    // EFFECTS: returns a Songs instance of the contents of the file
    //          throws ParsingError if it cannot be read.
    //          throws FileNotFoundException if file... is not found
    public static Songs read(String c) throws FileNotFoundException {
        File file = new File(c);
        FileType type = determineFileType(c);
        if (type == null) {
            throw new ParsingException("File " + c + " is not a valid playlist file.");
        }
        Songs result = new Songs();
        if (type == FileType.EXTM3U) {
            readExtM3U(c, result);
        } else if (type == FileType.PLS) {
            readPLS(c, result);
        }
        return result;
    }

    // EFFECTS: determines the file type of the file outline at path c
    //          throws FileNotFoundException if file isn't found
    private static FileType determineFileType(String c) throws FileNotFoundException {
        if (c.toLowerCase().endsWith(".pls") && isValidPLS(c)) {
            return FileType.PLS;
        } else if (c.toLowerCase().endsWith(".m3u") || c.toLowerCase().endsWith(".m3u8")) {
            return FileType.EXTM3U;
            // you could have a file named [playlist].wav and this will work. This may not work on all OSes.
        } else {
            return null;
        }
    }

    // EFFECTS: tests whether file C is a valid PLS file (but not whether the files exist)
    //          does not test whether file extension ends with .pls
    //          throws FileNotFoundException if the file isn't found
    private static boolean isValidPLS(String c) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(c));
        if (!scanner.hasNextLine() || !scanner.nextLine().trim().equals("[playlist]")) {
            return false;
        }
        int[] status = new int[2]; // current status of file. Explanation below.
        // index 1 is file number, index 2 is status of previous line
        // 0 = needs new file, 1 = title or length, 2 = length, 3 = Footer, 4 = finished footer
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (status[1] < 3 && line.startsWith("File" + (status[0] + 1) + "=")) {
                status[0]++;
                status[1] = 1;
            } else if (status[1] == 1 && line.startsWith("Title" + status[0] + "=")) {
                status[1] = 2;
            } else if ((status[1] == 1 || status[1] == 2) && line.startsWith("Length" + status[0] + "=")) {
                status[1] = 0;
            } else if (status[1] < 3 && line.startsWith("NumberOfEntries=" + status[0])) {
                status[1] = 3;
            } else if (status[1] == 3 && line.startsWith("Version=2")) {
                status[1] = 4;
            } else if (!line.equals("")) {
                return false;
            }
        }
        return status[1] == 4;
    }

    // MODIFIES: result
    // EFFECTS: parses the given string as an extended m3u file (with comments) - has to end with .m3u or .m3u8
    //          throws ParsingError if it cannot be read
    //          throws FileNotFoundException if file cannot be found
    //          returns result
    //          since M3U files can list other M3U files, it may infinitely recurse if they both point to each other
    private static Songs readExtM3U(String c, Songs result) throws FileNotFoundException {
        readCheckRecurseM3U(c, result, new ArrayList<>());
        return result;
    }

    // MODIFIES: songs
    // EFFECTS: adds SoundFile instance to songs, plus checks for infinite recursion in m3u file
    //          throws FileNotFoundException if files are not found.
    private static void readCheckRecurseM3U(String s, Songs songs, ArrayList<String> al) throws FileNotFoundException {
        assert (s.toLowerCase().endsWith(".m3u") || s.toLowerCase().endsWith(".m3u8"));
        if (al.contains(s)) {
            System.err.println("Infinite recursion on file: " + s + ", returning...");
            return;
        }
        Scanner input = new Scanner(new File(s));
        while (input.hasNextLine()) {
            String line = input.nextLine().trim();
            if (!line.startsWith("#") && !line.equals("")) {
                String location = new File(s).getParent() + File.separator + line;
                if (!new File(line).exists() && !new File(location).exists()) {
                    System.err.println("WARNING: " + line + ", nor " + location + " exists. Skipping line.");
                    continue;
                } else if (new File(line).exists()) {
                    location = line;
                }
                addFileToM3U(location, s, songs, al);
            }
        }
    }

    // MODIFIES: res
    // EFFECTS: adds sound files to songs instance (if possible)
    //          throws FileNotFoundException if files are not found
    private static void addFileToM3U(String s, String p, Songs res, ArrayList<String> al) throws FileNotFoundException {
        if (s.endsWith(".m3u") || s.endsWith(".m3u8")) {
            al.add(p);
            readCheckRecurseM3U(s, res, al);
        } else if (s.endsWith(".pls")) {
            readPLS(s, res);
        } else {
            res.add(s);
        }
    }

    // MODIFIES: result
    // EFFECTS: parses the given string as an .pls file - has to end with .pls
    //          throws ParsingError if it cannot be read
    //          throws FileNotFoundException if file cannot be found.
    private static Songs readPLS(String s, Songs result) throws FileNotFoundException {
        assert (s.toLowerCase().endsWith(".pls"));
        readCheckRecursePLS(s, result, new ArrayList<>());
        return result;
    }

    // MODIFIES: songs
    // EFFECTS: parses the given string as an .pls file - has to end with .pls
    //          immediately returns songs if pls parser has already seen this file
    //          throws ParsingError if it cannot be read
    //          throws FileNotFoundException if file cannot be found.
    private static void readCheckRecursePLS(String s, Songs songs, ArrayList<String> al) throws FileNotFoundException {
        assert (s.toLowerCase().endsWith(".pls"));
        if (al.contains(s)) {
            System.err.println("Infinitely recursive file! Returning...");
            return;
        }
        // NOTE: pls is recursive (i.e. can have .PLS files, but CANNOT HAVE m3u files)
        Scanner input = new Scanner(new File(s));
        int numFiles = 0;
        while (input.hasNextLine()) {
            String line = input.nextLine().trim();
            if (line.startsWith("File" + (numFiles + 1) + "=")) {
                numFiles++;
                if (line.endsWith(".pls")) {
                    al.add(s);
                    readCheckRecursePLS(new File(s).getParent() + SEP + readFileLinePLS(line), songs, al);
                } else {
                    attemptAddPLS(new File(s).getParent() + SEP, line, songs);
                }
            }
        }
    }

    // MODIFIES: songs
    // EFFECTS: attempts to add file to songs instance
    //          if fails, prints error msg to console
    private static void attemptAddPLS(String parent, String line, Songs songs) {
        String path = readFileLinePLS(line);
        if (!(new File(path).exists())) {
            path = parent + path;
        }
        int result = songs.add(path);
        if (result == Songs.ADDSTATUS_ERROR) {
            System.err.println("File from line: " + path + " may not exist or the sound class does not exist.");
        } else if (result == Songs.ADDSTATUS_UNSUPPORTED) {
            System.err.println("File from line: " + path + " is unsupported.");
        }
    }

    // EFFECTS: gets the file path of a given line "FileXX=file/path"
    //          does not check whether file number is valid (as it cannot read the whole file)
    //          throws ParsingException if line is not valid
    private static String readFileLinePLS(String line) {
        assert (line.matches("File[1-9]\\d*=.+"));
        int equalsIndex = line.indexOf("=");
        return line.substring(equalsIndex + 1);
    }
}
