package NameSayer;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;



/**
 * Class is the controller for the manage creations scene
 * handles the playing, deletion and viewing of creations
 */
public class ControllerManage implements Initializable {
    @FXML
    private Button menuButton;
    @FXML
    private Button playButton;

    @FXML
    private Button deleteButton;
    @FXML
    private TreeView<String> treeView;

    private ModelManage currentModel;

    @FXML
    private MediaView mv;

    private Media me;
    private MediaPlayer mp;


    /**
     * set up method for when the scene is first displayed,
     * populates the treeview
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTree();
        deleteButton.setDisable(true);
        playButton.setDisable(true);
    }


    /**
     * method creates an instance of the model manage class which handles the searching of
     * files in the current directory, then it adds the name of these files to the treeview
     */
    public void populateTree(){

        currentModel = new ModelManage();

        TreeItem<String> root = new TreeItem<>("Names");
        root.setExpanded(true);

        TreeItem<String>[] alphabet = new TreeItem[27];
        alphabet[26] = makeBranch("Others",root);
        for (int i = 0; i<26;i++){
            int sum = i+65;
            alphabet[i] = makeBranch(new String(new char[] {(char)sum}),root);
            alphabet[i].setExpanded(true);
        }


        for(String s:currentModel.get_creations()){
            String upper = s.toUpperCase();
            char[] charList = upper.toCharArray();
            int index = (int)charList[0] -65;
            if(index >=0 && index <=25) {
                makeBranch(s, alphabet[index]);
            }else{
                makeBranch(s,alphabet[26]);
            }
        }
        treeView.setRoot(root);
    }

    //Makes branch for tree
    public TreeItem<String> makeBranch(String title, TreeItem<String> parent){
        TreeItem<String> item = new TreeItem<>(title);
        parent.getChildren().add(item);
        return item;
    }


    /**
     * goes back to the main menu
     */
    public void menuButtonPressed(){

        Scene myScene= menuButton.getScene();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("NameSayerMainMenu.fxml"));
            Stage currentStage = (Stage) myScene.getWindow();
            currentStage.setScene(new Scene(root, 1280, 800));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * plays the selection on the tree view
     * if empty the play button does nothing
     * also disables some buttons which playing
     */
    public void playButtonPressed(){
        TreeItem item = treeView.getSelectionModel().getSelectedItem();
        if(item!=null){

            String name = (String) item.getValue();
            if(name.contains(".mp4")) {
                menuButton.setDisable(true);
                playButton.setDisable(true);
                deleteButton.setDisable(true);
                mp.play();
                mp.setOnEndOfMedia(new Runnable() {
                    @Override
                    public void run() {
                        menuButton.setDisable(false);
                        deleteButton.setDisable(false);
                        playButton.setDisable(false);
                    }
                });
        }
        }

    }



    /**
     * deletes the selection on the tree view
     * if empty the play button does nothing
     * also disables some buttons which playing
     */
    public void deleteButtonPressed(){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(" Delete ");
        alert.setHeaderText("Delete Creation");
        alert.setContentText("Are you sure you want to delete this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // ... user chose OK

            TreeItem item = treeView.getSelectionModel().getSelectedItem();
            if(item!=null) {
                String name = (String) item.getValue();
                if (name.contains(".mp4")) {
                    ProcessBuilder remove = new ProcessBuilder("/bin/bash", "-c", "rm " + "'" +"Creations/"+ name + "'");
                    try {
                        Process process = remove.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    populateTree();
                }
            }
        }


    }

    public void onSelect(){

        TreeItem item = treeView.getSelectionModel().getSelectedItem();
        if(item!=null){

            String name = (String) item.getValue();
            if(name.contains(".mp4")) {
                playButton.setDisable(false);
                deleteButton.setDisable(false);
                String path = new File("Creations/" + name).getAbsolutePath();
                me = new Media(new File(path).toURI().toString());
                mp = new MediaPlayer(me);
                mv.setMediaPlayer(mp);
            }else{
                playButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        }else{
            playButton.setDisable(true);
            deleteButton.setDisable(true);
        }

    }





}
