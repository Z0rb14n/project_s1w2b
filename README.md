# My Personal Project

## A start of a music player application

This application is supposed to be a music player. This is intended for the average person (with a copy of Java 8). This project is of interest to me because it implements both audio and graphics, and this project can potentially be extended to other applications, such as game development.

## User stories

This is my user story:
 - The user can interact with the music player, such as through a console or a GUI.
 - The user can add song(s) to the player and play them
 - The user can remove song(s) from the music player
 - The user can change audio volume if they do not want to hit the "mute" button on their keyboard
 - The user can see the songs in the list
 - The user can create playlists of songs
 - The user can loop songs
 - The user can save created playlists to a file
 - The user can open created playlists
 
 ## Using the interface
 - To add a song, please use the upper menu under File/Open/Song
 - To add a playlist, please use the upper menu under File/Open/Playlist
 - To save the playlist to a default location (./data/name_of_playlist.m3u), use File/Save
 - To save the playlist to a custom location, use File/Save as
   - NOTE: Save-as will NOT change file directory for Save function - "Save" saves to default location.
 - To create a new playlist, use Playlist/New
 - To switch to a different playlist, use Playlist/Switch to
 - To delete a playlist (except Main), use Playlist/Delete
 - To delete songs, right click on them and hit remove
 - To play specific songs, right click on them and hit play
 - To stop specific songs, right click on them and hit stop
 - To reset the app, do File/Reset
 - To clear the current playlist, do File/Clear
 - To adjust volume, use the bottom bar
 - To adjust the current time position, use the bottom bar
 - To play the next song/loop, use the bottom bar
 
 ## Phase 4, Task 2 Information:
 - Made a robust class - PlaylistParser (getSongs(file) throws checked and unchecked exceptions)
   - Throws checked exception FileNotFoundException - tested for
   - Throws unchecked exception Parsing Error - also tested for
 - Used map interface (utilized HashMap - see Playlists class)
 - Type hierarchy in Button class - overrides onClick();
 
 ## Phase 4, Task 3 Information:
 - Design Diagram is in ./data/UML_Design_Diagram.png
 - Issues with coupling among Buttons, Sliders, PlaylistRow and PlaylistView with App/Playlists/Songs/SoundFile
   - Reduced coupling via calls to App class instead of calls to all the classes
   - Removed App's dependency on FileType - all saving is done via Playlists (to increase cohesion among App class)
 - Numerous instances of duplicate code:
   - Duplicate code among various UI classes (e.g. buttons, sliders and PlaylistRow) - separate functions were defined in App class to reduce duplication among classes
     - This duplication is also a cohesion issue, as the buttons should not need to make that many function calls when it is the responsibility of the other classes (i.e. App/Playlists)
   - Duplicate code among conversion of microseconds to Minutes:Seconds - moved conversion to SoundFile class
   - Duplicate code among Button UI classes - created abstract ImageButton class
   - Duplicate code among Slider UI classes - created abstract Slider class