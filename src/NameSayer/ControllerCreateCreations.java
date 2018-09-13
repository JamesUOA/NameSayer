package NameSayer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
//Author: James Zhang

/**
 *
 * This class is the controller for the create creations screen
 * It handles the logic for creating creations and is paired with the CreateCreations fxml as the view
 *
 */
public class ControllerCreateCreations implements Initializable {

    // setting tick rate of progress indicator
    public static final long TICK_RATE = 50;
    // static field for the pop up window for overwrite check
    protected static boolean overwrite;
    //Stores the name of current creation
    private String _name;


    //FXML components sucha s buttons Labls Mediaview etc
    @FXML
    private Button _mainMenu;

    @FXML
    private Button _createNew;

    @FXML
    private Button _preview;
    @FXML
    private Button _Redo;

    @FXML
    private Button _delete;

    @FXML
    private TextField _fullName;

    @FXML
    private Button _recordButton;

    @FXML
    private ProgressIndicator _indicator;

    private Timer _progressTimer;

    @FXML
    private Label _status;

    @FXML
    private MediaView mv;


    /**
     * initialize method called every time the screen is first displayed, handles the set up of the screen
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _delete.setDisable(true);
        _preview.setDisable(true);
        _Redo.setDisable(true);
        _recordButton.setDisable(true);
        overwrite=true;


    }


    /**
     *
     *
     * Returns to the main menu when the menu button is pressed
     */
    public void menuButtonPressed(){

        Scene myScene= _mainMenu.getScene();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("NameSayerMainMenu.fxml"));
            Stage currentStage = (Stage) myScene.getWindow();
            currentStage.setScene(new Scene(root, 1280, 800));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * when the create new button is pressed the name is saved into the test
     * when a valid name is entered and create new is pressed the create button is disabled
     * the name is auto saved unless the user selects the delete key
     */
    public void createNewPressed(){

        _name = _fullName.getText();
        boolean messedUpName =  _name.equals("") || _name.trim().equals("") || (!_name.matches("[-\\sa-zA-Z \\d _]*"));
        //Checking for invalid names
        if(!messedUpName) {
            _createNew.setDisable(true);
            ModelManage creations = new ModelManage();
            List<String> nameList = creations.get_creations();
            //Loops through and check if the name exists
            for (String currentName : nameList) {
                //If the name exists make a prompt which asks user if they wish to overwrite
                if (currentName.substring(0, currentName.length() - 4).equals(_name)) {
                    OverWriteAlertBoxC alert = new OverWriteAlertBoxC();
                    alert.display();
                }
            }
            //overwrite is set to true by default and unless the user chooses not to overwrite then the existing creation would be overwritten
            if (overwrite) {
                _recordButton.setDisable(false);
                _fullName.setDisable(true);
                ProcessBuilder remove = new ProcessBuilder("/bin/bash", "-c", "rm " + "'" +"Creations/"+ _name + ".mp4" + "'");
                try {
                    Process process = remove.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else{
                _createNew.setDisable(false);
            }
            overwrite = true;

        }
    }


    /**
     * handles when the record button is pressed
     * creates a new instance fo the CreateCreation then calls start()
     * executing it on a seperate thread
     */

    public void recordButtonPressed(){
        _mainMenu.setDisable(true);
        _name = _fullName.getText();
        CreateCreation newCreation = new CreateCreation(_name);
        _recordButton.setDisable(true);
        newCreation.start();
        startIndicator();

    }

    /**
     *calls the bash command to remove the file when the delete button is pressed
     */
    public void deletePressed(){

        ProcessBuilder remove = new ProcessBuilder("/bin/bash", "-c", "rm "+ "'" +"Creations/" +_name +".mp4"+ "'");
        try {
            Process process = remove.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        _delete.setDisable(true);
        _preview.setDisable(true);
        _Redo.setDisable(true);
    }

    public void previewPressed(){
        _delete.setDisable(true);
        _Redo.setDisable(true);
        _createNew.setDisable(true);
        _mainMenu.setDisable(true);
        _fullName.setDisable(true);
        _preview.setDisable(true);
        String path = new File("Creations/"+_name+".mp4").getAbsolutePath();
        Media me = new Media(new File(path).toURI().toString());
        MediaPlayer mp = new MediaPlayer(me);
        mv.setMediaPlayer(mp);
        mp.play();
        mp.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                _preview.setDisable(false);
                _delete.setDisable(false);
                _Redo.setDisable(false);
                _createNew.setDisable(false);
                _mainMenu.setDisable(false);
                _fullName.setDisable(false);
            }
        });
    }

    /**
     * when the redo button is pressed
     */
    public void RedoPressed(){
        deletePressed();
        createNewPressed();
    }


    /**
     * StartIndicator is an important part of the GUI as it re-enables disabled buttons
     * as well as making a progress indicator
     */
    private void startIndicator(){
        _status.setText("Recording");
        _indicator.setProgress(0);
        _progressTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (_indicator.getProgress() >= 1) {
                    _progressTimer.cancel();
                    Platform.runLater(()-> _status.setText("Idle"));
                    _delete.setDisable(false);
                    _preview.setDisable(false);
                    _Redo.setDisable(false);
                    _fullName.setDisable(false);
                    _createNew.setDisable(false);
                    _mainMenu.setDisable(false);
                } else {
                    Platform.runLater(() -> _indicator.setProgress(_indicator.getProgress() + 0.01));
                }
            }
        };
        _progressTimer.scheduleAtFixedRate(timerTask, 0, TICK_RATE);
        _indicator.setProgress(0);

    }









}
