package NameSayer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerMainMenu implements Initializable {


    /**
     * class handles the changing of scenes from the main menu
     * self explanatory so minimal comments added
     */
    @FXML
    private Button _createButton;
    @FXML
    private Button _manageCreations;
    @FXML
    private Button _quitButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new File("Creations").mkdir();
    }

    public void manageCreationsButtonPressed() {
        Scene myScene= _manageCreations.getScene();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ManageCreations.fxml"));
            Stage currentStage = (Stage) myScene.getWindow();
            currentStage.setScene(new Scene(root, 1280, 800));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createButtonPressed(){

        Scene myScene= _createButton.getScene();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("CreateCreations.fxml"));
            Stage currentStage = (Stage) myScene.getWindow();
            currentStage.setScene(new Scene(root, 1280, 800));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void quitButtonPressed(){
        Stage stage = (Stage) _quitButton.getScene().getWindow();
        stage.close();
    }


}


