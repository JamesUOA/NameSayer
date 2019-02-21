package NameSayerAlpha.ManageControllers;

import NameSayerAlpha.*;

public class ManageControllers {

    /**
     * Class used to pass the controllers of different FXML's around
     * this is done because the frame handles most of the playing of
     * files and screens need access to it
     */

    private FrameController frame;
    private PlayingController playingController;
    private ListenScreenController browse;
    private RecordScreenController practice;
    private PlaylistController playlist;
    private static ManageControllers controllers;


    private ManageControllers(){

    }
    public static ManageControllers getInstance(){

        if(controllers == null){
            controllers = new ManageControllers();
        }
        return controllers;
    }

    public void setFrame(FrameController frame){
        this.frame = frame;
    }

    public void setPlayingController(PlayingController playingController){
        this.playingController = playingController;
    }

    public void setBrowse(ListenScreenController browse) {
        this.browse = browse;
    }

    public void setPractice(RecordScreenController practice) {
        this.practice = practice;
    }

    public void setPlaylist(PlaylistController playlist) {
        this.playlist = playlist;
    }

    public ListenScreenController getBrowse() {
        return browse;
    }

    public PlaylistController getPlaylist() {
        return playlist;
    }

    public RecordScreenController getPractice() {
        return practice;
    }

    public FrameController getFrame() {
        return frame;
    }


    public PlayingController getPlayingController() {
        return playingController;
    }
}
