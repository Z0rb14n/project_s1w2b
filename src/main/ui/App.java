package ui;

import java.util.ArrayList;
import java.util.Scanner;

// Represents the app that takes in input
public class App {
    private static App activeApp;
    private Scanner inputScan;
    private boolean exit;
    private Playlists playlists;
    // These represent function codes/function calls
    //<editor-fold desc="Return codes">
    public static final int PLAY_EMPTY = Playlists.PLAY_EMPTY;
    public static final int PLAY_SUCCESS = Playlists.PLAY_SUCCESS;
    //</editor-fold>
    //<editor-fold desc="Function Codes/Commands">
    private static final int APP_INVALID = -1;
    private static final int APP_EXIT = 0;
    private static final int APP_RESET = 1;
    private static final int APP_ADD = 2;
    private static final String APP_ADDCMD = "/add ";
    private static final int APP_ADDCMDLEN = APP_ADDCMD.length();
    private static final int APP_REMOVE = 3;
    private static final String APP_REMCMD = "/remove ";
    private static final int APP_REMCMDLEN = APP_REMCMD.length();
    private static final int APP_PLAY = 4;
    private static final String APP_PLAYCMD = "/play ";
    private static final int APP_PLAYCMDLEN = APP_PLAYCMD.length();
    private static final int APP_STOP = 5;
    private static final int APP_HELP = 6;
    private static final int APP_PAUSE = 7;
    private static final int APP_LIST = 8;
    private static final int APP_MUTE = 9;
    private static final String APP_VOLCMD = "/volume ";
    private static final int APP_VOLCMDLEN = APP_VOLCMD.length();
    private static final int APP_VOL = 10;
    private static final int APP_PLAYNEXT = 11;
    private static final int APP_LOOPLIST = 12;
    private static final int APP_DISABLE_LOOP_LIST = 13;
    private static final int APP_LOOP = 14;
    private static final int APP_NOLOOP = 15;
    private static final int APP_LISTPLAYLIST = 16;
    private static final int APP_UNMUTE = 17;
    private static final String APP_REMPLAYLISTCMD = "/remPlaylist ";
    private static final int APP_REMPLAYLISTCMDLEN = APP_REMPLAYLISTCMD.length();
    private static final int APP_REMPLAYLIST = 18;
    private static final String APP_NEWPLAYLISTCMD = "/newPlaylist ";
    private static final int APP_NEWPLAYLISTCMDLEN = APP_NEWPLAYLISTCMD.length();
    private static final int APP_NEWPLAYLIST = 19;
    private static final String APP_SWITCHPLAYLISTCMD = "/switchTo ";
    private static final int APP_SWITCHPLAYLISTCMDLEN = APP_SWITCHPLAYLISTCMD.length();
    private static final int APP_SWITCHPLAYLIST = 20;
    private static final int APP_SAVE = 21;
    private static final int APP_SAVEALL = 22;
    private static final String APP_SAVEASCMD = "/saveWithExt ";
    private static final int APP_SAVEASCMDLEN = APP_SAVEASCMD.length();
    private static final int APP_SAVEAS = 23;
    private static final String APP_SAVEEXTCMD = "/saveExt ";
    private static final int APP_SAVEEXTCMDLEN = APP_SAVEEXTCMD.length();
    private static final int APP_SAVEEXT = 24;
    private static final String APP_OPENNEWCMD = "/open ";
    private static final int APP_OPENNEWLEN = APP_OPENNEWCMD.length();
    private static final int APP_OPENNEW = 25;
    //</editor-fold>

    // EFFECTS: Initializes all variables and runs the application
    //          does not indefinitely loop if code is not a commandLineApplication
    private App(boolean isCommandLine) {
        init();
        exit = !isCommandLine;
        runApp();
    }

    // MODIFIES: this
    // EFFECTS: creates a signle instance of a command-line App
    public static App createCommandLineApp() {
        if (activeApp == null) {
            activeApp = new App(true);
        }
        return activeApp;
    }

    // MODIFIES: this
    // EFFECTS: creates a single instance of a non-commandline App if it does not exist
    public static App getActiveApp() {
        if (activeApp == null) {
            activeApp = new App(false);
        }
        return activeApp;
    }

    // MODIFIES: this
    // EFFECTS: runs the application
    public void runApp() {
        while (!exit) {
            typeMenu();
            String input = inputScan.nextLine();
            processInput(input);
        }
    }

    // MODIFIES: this
    // EFFECTS: resets the app
    public void reset() {
        playlists.reset();
        exit = false;
    }

    //<editor-fold desc="Terminal input" default-state="collapsed">

    // MODIFIES: this
    // EFFECTS: process the input of the program
    public void processInput(String i) {
        int fnCode = stringToFunctionCode(i);
        if (handleBasicInput(fnCode)) {
            return;
        }
        handleAdvancedInput(i);
    }

    // MODIFIES: this
    // EFFECTS: handles inputs of adds/removes/plays/volume changes/playlist creations,deletions,switches
    private void handleAdvancedInput(String i) {
        switch (stringToFunctionCode(i)) {
            case APP_ADD:
                add(i.substring(APP_ADDCMDLEN));
                return;
            case APP_REMOVE:
                remove(i.substring(APP_REMCMDLEN));
                return;
            case APP_PLAY:
                play(i.substring(APP_PLAYCMDLEN));
                return;
            case APP_VOL:
                volCmd(i.substring(APP_VOLCMDLEN));
                return;
            default:
                handlePlaylistRelatedInput(i);
        }
    }

    // MODIFIES: this
    // EFFECTS: performs the functions of playlist related input (e.g. switch/add/remove playlists)
    private void handlePlaylistRelatedInput(String input) {
        switch (stringToFunctionCode(input)) {
            case APP_NEWPLAYLIST:
                newPlaylist(input.substring(APP_NEWPLAYLISTCMDLEN));
                return;
            case APP_REMPLAYLIST:
                remPlaylist(input.substring(APP_REMPLAYLISTCMDLEN));
                return;
            case APP_SWITCHPLAYLIST:
                switchTo(input.substring(APP_SWITCHPLAYLISTCMDLEN));
                return;
            case APP_SAVEAS:
                saveAs(input.substring(APP_SAVEASCMDLEN));
                return;
            case APP_SAVEEXT:
                saveAsExt(input.substring(APP_SAVEEXTCMDLEN));
                return;
            case APP_OPENNEW:
                open(input.substring(APP_OPENNEWLEN));
        }
    }

    // MODIFIES: this
    // EFFECTS: opens a new playlist from file input
    public void open(String input) {
        playlists.open(input);
    }

    // MODIFIES: this
    // EFFECTS: handles inputs of invalids, exits, resets, stops, helps
    private boolean handleBasicInput(int fnCode) {
        switch (fnCode) {
            case APP_INVALID:
                System.out.println("Invalid input. ");
                return true;
            case APP_EXIT:
                exit();
                return true;
            case APP_RESET:
                reset();
                return true;
            case APP_STOP:
                stop();
                return true;
            case APP_HELP:
                helpPart1();
                return true;
            default:
                return handleBasicInput2(fnCode);
        }
    }

    // MODIFIES: this
    // EFFECTS: handles inputs of lists, mutes, pauses, playnext, playlist looping
    private boolean handleBasicInput2(int fnCode) {
        switch (fnCode) {
            case APP_LIST:
                list();
                return true;
            case APP_PAUSE:
                pause();
                return true;
            case APP_PLAYNEXT:
                playNext();
                return true;
            case APP_LISTPLAYLIST:
                listPlaylists();
                return true;
        }
        return handleBasicInputMuteLooping(fnCode);
    }

    // MODIFIES: this
    // EFFECTS: handles inputs of listing playlists and unmuting, looping/nolooping
    private boolean handleBasicInputMuteLooping(int fnCode) {
        switch (fnCode) {
            case APP_UNMUTE:
                unmute();
                return true;
            case APP_MUTE:
                mute();
                return true;
            case APP_LOOPLIST:
                playlistLoop();
                return true;
            case APP_DISABLE_LOOP_LIST:
                disablePlaylistLoop();
                return true;
            case APP_LOOP:
                loop();
                return true;
            case APP_NOLOOP:
                noLoop();
                return true;
        }
        return handlePlaylistSavingBasic(fnCode);
    }

    // MODIFIES: this
    // EFFECTS: handles saving playlists (with zero arguments)
    private boolean handlePlaylistSavingBasic(int fnCode) {
        switch (fnCode) {
            case APP_SAVE:
                save();
                return true;
            case APP_SAVEALL:
                saveAll();
                return true;
            default:
                return false;
        }
    }

    // EFFECTS: returns the corresponding function code to input string
    private static int stringToFunctionCode(String i) {
        int result = basicStringToFunctionCode(i);
        if (result != APP_INVALID) {
            return result;
        }
        if (i.startsWith(APP_ADDCMD) && i.length() > APP_ADDCMDLEN) {
            return APP_ADD;
        } else if (i.startsWith(APP_REMCMD) && i.length() > APP_REMCMDLEN) {
            return APP_REMOVE;
        } else if (i.startsWith(APP_PLAYCMD) && i.length() > APP_PLAYCMDLEN) {
            return APP_PLAY;
        } else if (i.startsWith(APP_VOLCMD) && i.length() > APP_VOLCMDLEN) {
            return APP_VOL;
        }
        return stringToFunctionCodePlaylist(i);
    }

    // EFFECTS: returns the corresponding function code to input string
    //          mainly, playlist related functionality (saving, switching, new)
    private static int stringToFunctionCodePlaylist(String i) {
        if (i.startsWith(APP_SWITCHPLAYLISTCMD) && i.length() > APP_SWITCHPLAYLISTCMDLEN) {
            return APP_SWITCHPLAYLIST;
        } else if (i.startsWith(APP_NEWPLAYLISTCMD) && i.length() > APP_NEWPLAYLISTCMDLEN) {
            return APP_NEWPLAYLIST;
        } else if (i.startsWith(APP_REMPLAYLISTCMD) && i.length() > APP_REMPLAYLISTCMDLEN) {
            return APP_REMPLAYLIST;
        } else if (i.startsWith(APP_SAVEASCMD) && i.length() > APP_SAVEASCMDLEN) {
            return APP_SAVEAS;
        } else if (i.startsWith(APP_SAVEEXTCMD) && i.length() > APP_SAVEEXTCMDLEN) {
            return APP_SAVEEXT;
        } else if (i.startsWith(APP_OPENNEWCMD) && i.length() > APP_OPENNEW) {
            return APP_OPENNEW;
        }
        return APP_INVALID;
    }

    // EFFECTS: returns the corresponding function code from specific input strings
    //          namely, exit/quit/reset/stopAll/help/list/pause/playNext
    private static int basicStringToFunctionCode(String i) {
        i = i.trim();
        if (i.equals("/exit") || i.equals("/quit")) {
            return APP_EXIT;
        } else if (i.equals("/reset")) {
            return APP_RESET;
        } else if (i.equals("/stop")) {
            return APP_STOP;
        } else if (i.equals("/help")) {
            return APP_HELP;
        } else if (i.equals("/list")) {
            return APP_LIST;
        } else if (i.equals("/pause")) {
            return APP_PAUSE;
        } else if (i.equals("/playnext")) {
            return APP_PLAYNEXT;
        }
        return basicStringToFunctionCodePlaylistMute(i);
    }

    // EFFECTS: returns the corresponding function code from specific input strings
    //          namely, playlist-related functionality and mutes
    private static int basicStringToFunctionCodePlaylistMute(String i) {
        if (i.equals("/playlistLoop")) {
            return APP_LOOPLIST;
        } else if (i.equals("/disablePlaylistLoop")) {
            return APP_DISABLE_LOOP_LIST;
        } else if (i.equals("/loop")) {
            return APP_LOOP;
        } else if (i.equals("/noloop")) {
            return APP_NOLOOP;
        } else if (i.equals("/mute")) {
            return APP_MUTE;
        } else if (i.equals("/unmute")) {
            return APP_UNMUTE;
        } else if (i.equals("/listPlaylists")) {
            return APP_LISTPLAYLIST;
        }
        return basicStringToFunctionCodePlaylistSaving(i);
    }

    // EFFECTS: returns the corresponding function code for specific input strings
    //          mainly, playlist saving with no arguments
    private static int basicStringToFunctionCodePlaylistSaving(String i) {
        if (i.equals("/save")) {
            return APP_SAVE;
        } else if (i.equals("/saveAll")) {
            return APP_SAVEALL;
        }
        return APP_INVALID;
    }

    //</editor-fold>

    // EFFECTS: returns the list of all playlist names in the playlist.
    public ArrayList<String> listPlaylistNames() {
        return playlists.listPlaylistNames();
    }

    // EFFECTS: reutrns currently active playlist name
    public String getCurrentlyUsed() {
        return playlists.getCurrentlyUsed();
    }

    // MODIFIES: this
    // EFFECTS: unmutes app
    public void unmute() {
        playlists.unmute();
    }

    // EFFECTS: saves active playlist
    public void save() {
        playlists.save();
    }

    // EFFECTS: saves all playlists
    public void saveAll() {
        playlists.saveAll();
    }

    // EFFECTS: saves active playlist as given name
    public void saveAs(String name) {
        playlists.saveAs(name);
    }

    // EFFECTS: saves active playlist without using default file folder
    public void saveAsNonDefault(String name) {
        playlists.saveAsNonDefault(name);
    }

    // EFFECTS: saves active playlist as given file type
    public void saveAsExt(String ext) {
        playlists.saveWithExt(ext);
    }

    // MODIFIES: this
    // EFFECTS: plays next file
    public int playNext() {
        return playlists.playNext();
    }

    // MODIFIES: this
    // EFFECTS: enables looping of one song
    public void loop() {
        playlists.loop();
    }

    // MODIFIES: this
    // EFFECTS: disables looping of one song
    public void noLoop() {
        playlists.noLoop();
    }

    // MODIFIES: this
    // EFFECTS: loops the playlist starting from beginning or currently playing song
    public void playlistLoop() {
        playlists.enablePlaylistLoop();
    }

    // MODIFIES: this
    // EFFECTS: disables looping the playlist.
    public void disablePlaylistLoop() {
        playlists.disablePlaylistLoop();
    }

    // MODIFIES: this
    // EFFECTS: changes volume according to input
    public void volCmd(String i) {
        try {
            float c = Float.parseFloat(i);
            playlists.volumeAdjust(c);
        } catch (NumberFormatException e) {
            System.out.println("Invalid argument: " + i);
        }
    }

    // MODIFIES: this
    // EFFECTS: changes volume according to input
    public void volume(float f) {
        playlists.volumeAdjust(f);
    }

    // MODIFIES: this
    // EFFECTS: sets volume to lowest setting (may not be inaudible)
    public void mute() {
        playlists.mute();
    }

    // EFFECTS: lists all songs in console
    public void list() {
        playlists.list();
    }

    // EFFECTS: lists all playlists
    public void listPlaylists() {
        playlists.listPlaylists();
    }

    // MODIFIES: this
    // EFFECTS: pauses currently playing song
    public void pause() {
        playlists.pause();
    }

    // EFFECTS: shows the help menu in the console.
    public void helpPart1() {
        System.out.println("To get back to this menu, type /help.");
        System.out.println("Typing /exit or /quit will exit the program.");
        System.out.println("Typing /add <string> adds the <string> into the list of songs.");
        System.out.println("Typing /remove <string> removes the <string> from the list of songs.");
        System.out.println("Typing /stop stops all songs from playing, unless they are playlist looping.");
        System.out.println("Typing /reset will reset the program.");
        System.out.println("Typing /pause will pause the currently playing song.");
        System.out.println("Typing /list will list all songs.");
        System.out.println("Typing /mute will mute all playlists.");
        System.out.println("Typing /unmute will... unmute it");
        System.out.println("Typing /loop will loop the currently playing song until the cows come home.");
        System.out.println("Typing /noloop will stop that.");
        System.out.println("Typing /volume <decimal value from range 6.0206 to -80> will adjust the volume (in dB).");
        System.out.println("Typing /loopPlaylist will loop the whole playlist.");
        System.out.println("Typing /disablePlaylistLoop will stop looping.");
        helpPart2();
    }

    // EFFECTS: shows another part of the help menu to console
    public void helpPart2() {
        System.out.println("Typing /listPlaylists lists all playlists. Go figure.");
        System.out.println("Typing " + APP_REMPLAYLISTCMD + "removes playlist name from list (main can't be removed)");
        System.out.println("Typing " + APP_SWITCHPLAYLISTCMD + "switches playlist to a different playlist.");
        System.out.println("Typing " + APP_NEWPLAYLISTCMD + "adds a new playlist.");
        System.out.println("Typing /save saves the currently active playlist to a file.");
        System.out.println("Typing /saveall saves all playlists to files.");
        System.out.println("Typing " + APP_SAVEASCMD + " saves currently active file to given name.");
        System.out.println("Typing " + APP_SAVEEXTCMD + " saves currently active file to given file extension.");
        System.out.println("Typing " + APP_OPENNEWCMD + " creates a new playlist from that file.");

    }

    // MODIFIES: this
    // EFFECTS: adds the given string to song list
    //          returns status code of playlist
    public int add(String s) {
        return playlists.add(s);
    }

    // MODIFIES: this
    // EFFECTS: removes the given file
    public void remove(String c) {
        playlists.remove(c);
    }

    // MODIFIES: this
    // EFFECTS: removes file of given index (convenience method)
    public void remove(int index) {
        playlists.remove(index);
    }

    // MODIFIES: this
    // EFFECTS: stops all playing soundfiles.
    public void stop() {
        playlists.stop();
    }

    // MODIFIES: this
    // EFFECTS: stops soundfile at specific index from playing, if it is playing
    public void stop(int index) {
        if (index >= getCurrentPlaylistLength()) {
            return;
        }
        if (isSongPlaying(index)) {
            playlists.stop();
        }
    }

    // MODIFIES: this
    // EFFECTS: exits the program in the next loop cycle.
    public void exit() {
        exit = true;
        System.out.println("Exiting...");
        playlists.cleanup();
        System.exit(0);
    }

    // MODIFIES: this
    // EFFECTS: plays the selected file if exists
    public void play(String s) {
        playlists.play(s);
    }

    // MODIFIES: this
    // EFFECTS: initializes variables and startup
    public void init() {
        playlists = new Playlists();
        exit = false;
        inputScan = new Scanner(System.in);
    }

    // EFFECTS: gets whether a song is currently playing
    public boolean isPlaying() {
        return playlists.isPlaying();
    }

    // EFFECTS: prints the menu to the console
    public void typeMenu() {
        System.out.println("Type a command...");
    }

    // MODIFIES: this
    // EFFECTS: removes playlist from playlists
    public void remPlaylist(String input) {
        playlists.removePlaylist(input);
    }

    // MODIFIES: this
    // EFFECTS: adds a new playlist of given name
    public void newPlaylist(String input) {
        playlists.makeNew(input);
    }

    // MODIFIES: this
    // EFFECTS: switches active playlist to one of given name
    public void switchTo(String input) {
        playlists.switchTo(input);
    }

    // MODIFIES: this
    // EFFECTS: jumps playhead to current location
    public int jumpTo(long micros) {
        return playlists.jumpTo(micros);
    }

    // MODIFIES: this
    // EFFECTS: plays previous song
    public int playPrev() {
        return playlists.playPrev();
    }

    // MODIFIES: this
    // EFFECTS: clears current playlist
    public void clearCurrent() {
        playlists.clearCurrent();
    }

    //<editor-fold desc="Convenience Methods">

    // EFFECTS: returns last index played (convenience method)
    public int getLastIndexPlayed() {
        return playlists.getLastIndexPlayed();
    }

    // EFFECTS: returns current index of song playing, -1 if none (convenience method)
    public int getCurrentIndexPlaying() {
        return playlists.getCurrentIndexPlaying();
    }

    // EFFECTS: returns whether or not the current Songs instance is empty (convenience method)
    public boolean isCurrentPlaylistEmpty() {
        return playlists.isCurrentPlaylistEmpty();
    }

    // EFFECTS: gets current position in song in microseconds (convenience method)
    public long getCurrentMicrosecondPosition() {
        return playlists.getCurrentMicrosecondPosition();
    }

    // EFFECTS: gets current song's length in microseconds (convenience method)
    public long getCurrentMicrosecondLength() {
        return playlists.getCurrentMicrosecondLength();
    }

    // EFFECTS: gets song's length in microseconds (convenience method)
    public long getMicrosecondLength(int index) {
        return playlists.getMicrosecondLength(index);
    }

    // EFFECTS: gets last last played's paused time position
    public long getLastPausedMicrosecondPosition() {
        return playlists.getLastPausedMicrosecondPosition();
    }

    // EFFECTS: gets current playlist's length (i.e. number of songs)
    public int getCurrentPlaylistLength() {
        return playlists.getCurrentPlaylistLength();
    }

    // EFFECTS: returns whether or not the current playlist is paused
    public boolean isCurrentPlaylistPaused() {
        return playlists.isCurrentPlaylistPaused();
    }

    // EFFECTS: returns current volume
    public float getCurrentVolume() {
        return playlists.getVolume();
    }

    // EFFECTS: returns the song name of the given index in the current playlist (convenience method)
    public String getSongName(int index) {
        return playlists.getSongName(index);
    }

    // EFFECTS: returns time stamp of song of given index
    public String getSongTimeStamp(int index) {
        return playlists.getSongTimeStamp(index);
    }

    // EFFECTS: returns whether song is playing
    public boolean isSongPlaying(int index) {
        return playlists.isCurrentSongPlaying(index);
    }

    //</editor-fold>
}
