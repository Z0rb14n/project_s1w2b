package ui;

public class Main {

    // TODO: Potentially use Observers
    // TODO: Potentially use JavaFX Media instead of AudioSystem - supports MP3 Files

    public static void main(String[] args) {
        // NOTE: PLAYLISTS SAVE TO DEFAULT FOLDER

        // To determine accepted types on each system, do:
        // AudioFileFormat.Type[] types = AudioSystem.getAudioFileTypes();
        // for (AudioFileFormat.Type type : types) {
        //     System.out.println(type.getExtension());
        // }
        // Java supports WAV, AU, AIF. I don't think it supports others.
        //App app = App.createCommandLineApp();
        AppFrame af = new AppFrame(); // for graphical app
    }
}
