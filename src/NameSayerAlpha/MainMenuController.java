package NameSayerAlpha;

import NameSayerAlpha.ManageControllers.ManageControllers;
import NameSayerAlpha.nameObjects.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainMenuController {

    @FXML private Button login;
    @FXML private Button guest;
    @FXML private Button exit;
    @FXML private TextField textField;

    public void initialize() {
        setUp();
        badQualityTxtSetUp();
        textField.setStyle("-fx-text-inner-color: white");
    }

    /**
     * Sets up the required files for the program
     */
    public void setUp() {
        File playlists = new File(FilePaths.PLAY_LIST_LOCATION);
        if (!(playlists.exists() && playlists.isDirectory())) {
            playlists.mkdirs();
        }
        File local = new File(FilePaths.LOCAL_LOCATION);
        if (!(local.exists() && local.isDirectory())) {
            local.mkdirs();
        }
    }


    /**
     * Sets up the BadQuality Text which records all of the bad quality names
     */
    public void badQualityTxtSetUp() {

        File file = new File("BadQuality.txt");
        if(file.exists() && !file.isDirectory()) {
            // do nothing
        } else {
            //Creating and adding the header for the bad quality text.
            ProcessBuilder process = new ProcessBuilder("/bin/bash", "-c", "touch " + "BadQuality.txt");
            try {
                process.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * if the User chooses to login with a  name
     */
    public void loginButtonPressed(){
        if(!textField.getText().equals("")) {
            User.getUser().setName(textField.getText());
            loadFrame();
        }
    }

    /**
     * Allows the User to login as Guest
     */
    public void guestButtonPressed(){
        User.getUser().setName("Guest");
        loadFrame();
    }

    /**
     * loads the main frame of the program
     */
    private void loadFrame(){

        Parent root=null;
        Stage currentStage = (Stage) login.getScene().getWindow();
        FXMLLoader frameLoader = new FXMLLoader(getClass().getResource(FilePaths.FRAME_FXML));

        try {
            root = frameLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ManageControllers.getInstance().setFrame(frameLoader.getController());
        currentStage.setScene(new Scene(root, 1366, 768));
    }

    public void exitButtonPressed() {
        System.exit(0);
    }

}
