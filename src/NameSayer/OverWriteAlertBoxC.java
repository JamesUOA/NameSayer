package NameSayer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * this class is an alert box which handles the overwriting of files
 * when this class is called from the controller for create creations screen
 * it modifies the protected static field in that class to check if the user wants to overwrite
 */
public class OverWriteAlertBoxC {

    Stage alertStage;
    @FXML
    private Button _yes;
    @FXML
    private Button _no;



    public OverWriteAlertBoxC(){
        alertStage = new Stage();
        alertStage.initModality(Modality.APPLICATION_MODAL);
    }

    public void display( ){


        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("OverWriteAlertBox.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        alertStage.setTitle("Overwrite");
        alertStage.setScene(new Scene(root, 400, 200));

        //causes the alertbox to be dealt with first before the code continues to execute
        alertStage.showAndWait();
    }


    /**
     * two methods handle the I/O of the user
     */
    public void _yesButtonPressed(){
        ControllerCreateCreations.overwrite =true;
        Stage stage = (Stage) _yes.getScene().getWindow();
        stage.close();
    }

    public void _noButtonPressed(){

        ControllerCreateCreations.overwrite = false;
        Stage stage = (Stage) _no.getScene().getWindow();
        stage.close();
    }

}
