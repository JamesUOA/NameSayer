package NameSayerAlpha;

import NameSayerAlpha.ManageControllers.ManageControllers;
import NameSayerAlpha.nameObjects.Name;
import NameSayerAlpha.nameObjects.User;
import NameSayerAlpha.playlistManagers.CurrentPlaylist;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class PlayingController {

    @FXML
    private ListView playList;
    @FXML
    private Label name;
    @FXML
    private Label playListName;
    @FXML
    private Label firstCharacter;
    @FXML
    private Label currentNameLabel;

    private String currentPlaylist;
    private String currentName;

    public void initialize(){
        playList.setItems(CurrentPlaylist.getInstance().getNamesList());
        name.setText(User.getUser().getName());
    }

    public void setContent(String currentPlaylist,String currentName){
        this.currentPlaylist = currentPlaylist;
        this.currentName = currentName;
        firstCharacter.setText(currentPlaylist.substring(0,1).toUpperCase());
        currentNameLabel.setText(currentName);
        playListName.setText(this.currentPlaylist);
    }

    public void itemSelected(){

        if(playList.getSelectionModel().getSelectedItem()!=null) {
            Name selectedName = (Name) playList.getSelectionModel().getSelectedItem();
            currentNameLabel.setText(selectedName.toString());
            ManageControllers.getInstance().getFrame().clearQueue();
            ManageControllers.getInstance().getFrame().setSelection(selectedName.toString(), currentPlaylist);
            ManageControllers.getInstance().getFrame().addToQueue(selectedName);
        }
    }

    public void playAll(){
        ManageControllers.getInstance().getFrame().clearQueue();
        for(Name n:CurrentPlaylist.getInstance().getNamesList()){
            ManageControllers.getInstance().getFrame().addToQueue(n);
        }
        ManageControllers.getInstance().getFrame().playButtonClicked();
        if(ManageControllers.getInstance().getFrame().isShuffle()){
            ManageControllers.getInstance().getFrame().shufflePlaylist();
        }
    }



}
