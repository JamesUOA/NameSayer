package NameSayerAlpha.playlistManagers;

import NameSayerAlpha.nameObjects.Name;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CurrentPlaylist {

    /**
     * current selected playlist in
     */
    private Playlist playlist;
    private static ObservableList<Name> namesList;
    private static CurrentPlaylist instance;

    private CurrentPlaylist() {
    }

    public static CurrentPlaylist getInstance() {
        if(instance==null){
            namesList = FXCollections.observableArrayList();
            instance = new CurrentPlaylist();
        }
        return instance;
    }

    public void setPlaylist(Playlist playlist) {
        namesList.clear();
        namesList.addAll( playlist.getNames());
        this.playlist = playlist;
    }

    public ObservableList<Name> getNamesList() {
        return namesList;
    }

    public Playlist getPlaylist() {
        return playlist;
    }
}