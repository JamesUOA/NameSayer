package NameSayerAlpha;

import NameSayerAlpha.ManageControllers.ManageControllers;
import NameSayerAlpha.fileManagers.DatabaseNames;
import NameSayerAlpha.nameObjects.Name;
import NameSayerAlpha.nameObjects.NameComposite;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class ListenScreenController {

    @FXML private Label nameLabel;
    @FXML private JFXListView listView;
    @FXML private TextField searchField;
    @FXML private JFXListView<Name> playList;
    @FXML private TextField playListName;

    private FilteredList<Name> resultsList;
    private ObservableList<Name> currentPlaylist;


    public void initialize() {
        currentPlaylist = FXCollections.observableArrayList();


        ObservableList<Name> fullList = FXCollections.observableArrayList(DatabaseNames.getInstance().getNamesList());
        resultsList = new FilteredList<Name>(fullList, p -> true);
        listView.setItems(resultsList);
        playList.setItems(currentPlaylist);
        setUpDoubleClick();
    }

    public void playlistPressed() {
        if (playList.getSelectionModel().getSelectedItem() != null) {

            //Gets the controller of the frame then sets the values to selected items
            ManageControllers.getInstance().getFrame().setSelection(playList.getSelectionModel().getSelectedItem().toString(), "-");
            //debugging
            System.out.println(DatabaseNames.getInstance().getNamesList());
            ManageControllers.getInstance().getFrame().clearQueue();
            //adding to the play queue
            ManageControllers.getInstance().getFrame().addToQueue(playList.getSelectionModel().getSelectedItem());
        }
    }

    public void setUpDoubleClick() {
        listView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1) {
                String currentText = searchField.getText();
                if(currentText.lastIndexOf(" ")!=-1){
                    currentText = currentText.substring(0,currentText.lastIndexOf(" ")+1);
                    searchField.setText(currentText + listView.getSelectionModel().getSelectedItem().toString()+" ");
                }else {
                    searchField.clear();
                    searchField.setText(listView.getSelectionModel().getSelectedItem().toString() + " ");
                }

            }
        });
        playList.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1) {
                playlistPressed();
            } else if(mouseEvent.getClickCount() == 2){
                ManageControllers.getInstance().getFrame().playButtonClicked();
            }
        });
    }

    public void savePlaylist(){
        if(!playListName.getText().equals("")) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(FilePaths.PLAY_LIST_LOCATION +"/"+ playListName.getText().replaceAll("\\s+","_") + ".txt", "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            for(Name n: currentPlaylist) {
                writer.println(n.toString());
            }
            writer.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saved");
            alert.setHeaderText("Playlist saved");
            alert.setContentText(playListName.getText().replaceAll("\\s+","_") +"was created");
            alert.showAndWait();
            ManageControllers.getInstance().getPlaylist().updatePlaylists();

        }
    }
    public void removePressed(){
        if(playList.getSelectionModel().getSelectedItem()!=null){
            currentPlaylist.remove(playList.getSelectionModel().getSelectedItem());
        }
    }
    public void clearPressed(){
        currentPlaylist.clear();
    }

    public void textChanged() {
        Platform.runLater(this::updateList);
    }

    public void addToPlaylist() {
        if (searchField.getText() != null && !searchField.getText().equals("")) {
            String searchText = searchField.getText();
            String[] splited = searchText.split("\\s+");
            String fullName = "";

            for(String s:splited){
                fullName += s.substring(0,1).toUpperCase()+s.substring(1).toLowerCase()+" ";
            }
            fullName = fullName.substring(0,fullName.length()-1);

            NameComposite newName = new NameComposite(fullName);
            if(newName.load()) {
                currentPlaylist.add(newName);
                searchField.clear();
            }
        }
    }

    public void updateList() {
        String input = searchField.getText();
        int index = input.lastIndexOf(" ");
        if (index != -1) {
            input = input.substring(index + 1);
        }
        String finalInput = input;
        resultsList.setPredicate(e -> {
            if (finalInput == null || finalInput.isEmpty()) {
                return true;
            }

            // Compare first name and last name field in your object with filter.
            String lowerCaseFilter = finalInput.toLowerCase();
            if (String.valueOf(e.toString()).toLowerCase().startsWith(lowerCaseFilter)) {
                return true;
                // Filter matches first name.
            } else if (String.valueOf(e.toString()).toLowerCase().startsWith(lowerCaseFilter)) {
                return true; // Filter matches last name.
            }
            return false; // Does not match.
        });

    }

}


