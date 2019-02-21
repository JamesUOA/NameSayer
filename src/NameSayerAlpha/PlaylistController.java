package NameSayerAlpha;

import NameSayerAlpha.ManageControllers.ManageControllers;
import NameSayerAlpha.nameObjects.Name;
import NameSayerAlpha.playlistManagers.CurrentPlaylist;
import NameSayerAlpha.playlistManagers.Playlist;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class PlaylistController {
    @FXML
    private JFXListView playlists;
    @FXML
    private Button uploadButton;

    @FXML
    private JFXListView contentsList;

    private ObservableList<Playlist> playlistValues;
    private ObservableList contentsListValues;
    private File[] matchingFiles;
    private List<File> matchingFile = new ArrayList<>();

    public void initialize(){
        playlistValues = FXCollections.observableArrayList();
        contentsListValues = FXCollections.observableArrayList();
        playlists.setItems(playlistValues);
        contentsList.setItems(contentsListValues);
        updatePlaylists();

    }

    public void updatePlaylists(){
        playlistValues.clear();
        File f = new File(FilePaths.PLAY_LIST_LOCATION);
        matchingFiles = f.listFiles((dir, name) -> name.endsWith(".txt"));
        for (File file : matchingFiles) {
            matchingFile.add(file);
            int index = file.getName().lastIndexOf(".");
            Playlist playlist = new Playlist(file.getName().substring(0, index),file.toURI());
            playlist.load();
            playlistValues.add(playlist);
        }
    }


    public void onPlaylistSelect(){
        contentsListValues.clear();
        Playlist list = (Playlist) playlists.getSelectionModel().getSelectedItem();
        if(list!=null) {
            CurrentPlaylist.getInstance().setPlaylist(list);
            System.out.println(list.getNames());
            if (!(list.getNames().size() == 0)) {
                contentsListValues.addAll(list.getNames());
                ManageControllers.getInstance().getFrame().setSelection("-", list.toString());

            } else {
                contentsListValues.addAll("No valid items");
            }
        }
    }


    public void onNameSelect(){
        if(playlists.getSelectionModel().getSelectedItem()!=null && contentsList.getSelectionModel().getSelectedItem()!=null&& !contentsList.getSelectionModel().getSelectedItem().toString().equals("No valid items")) {
            String currentPlaylist = playlists.getSelectionModel().getSelectedItem().toString();
            String currentName = contentsList.getSelectionModel().getSelectedItem().toString();
            ManageControllers.getInstance().getFrame().setSelection(currentName, currentPlaylist);
            ManageControllers.getInstance().getFrame().clearQueue();
            ManageControllers.getInstance().getFrame().addToQueue((Name)contentsList.getSelectionModel().getSelectedItem());
        }
    }

    public void uploadButtonPressed(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if(file!=null) {
            int index = file.getName().lastIndexOf(".");
            Playlist playlist = new Playlist(file.getName().substring(0, index),file.toURI());
            playlist.load();
            File temp=new File(FilePaths.PLAY_LIST_LOCATION + "/"+file.getName());
            try {
                Files.deleteIfExists(temp.toPath());
                Files.copy(file.toPath(),temp.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            playlistValues.add(playlist);
            matchingFile.add(file);
        }
    }

    public void deletePlaylist(){

        if(playlistValues.size()!=0 && playlists.getSelectionModel().getSelectedItem()!=null){
            String fileName = playlists.getSelectionModel().getSelectedItem().toString();
            playlistValues.remove(playlists.getSelectionModel().getSelectedItem());
            File file = new File(FilePaths.PLAY_LIST_LOCATION+ "/" + fileName+".txt");
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void clearPlaylist(){
        File index = new File(FilePaths.PLAY_LIST_LOCATION);
        String[]entries = index.list();
        if(entries!=null) {
            for (String s : entries) {
                File currentFile = new File(index.getPath(), s);
                currentFile.delete();
            }
        }
        updatePlaylists();

    }
}
