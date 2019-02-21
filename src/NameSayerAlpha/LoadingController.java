package NameSayerAlpha;


import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LoadingController {
    @FXML
    private Label task;

    @FXML
    private Label file;

    public void setTask(String s){
        task.setText(s);
    }

    public void setFile(String s){
        file.setText(s);
    }
}