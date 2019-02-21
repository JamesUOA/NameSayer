package NameSayerAlpha.models;

import javafx.scene.control.Alert;

public class Achievement {

    /**
     * very simple system that gives achievements for whenever the user
     * practices a recording
     */
    private int numberOfRecordings;

    public Achievement() {
        //Initialise number of Recordings to be zero when Achievement objects is made.
       numberOfRecordings = 0;
    }

    public void add() {
        //Adds 1 to the number of recordings when method is called.
        numberOfRecordings++;
    }

    public int checkNumberOfRecordings() {
        return numberOfRecordings;
    }

    public void displayAchievement(int recordings) {
        if (recordings == 2) {
            alert(recordings);
        } else if (recordings == 5) {
            alert(recordings);
        } else if (recordings == 10) {
            alert(recordings);
        } else if (recordings == 20) {
            alert(recordings);
        } else if (recordings == 40) {
            alert(recordings);
        }
    }

    //Creates the alert dialog.
    private void alert(int number) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Achievement");
        alert.setHeaderText("Achievement Unlocked.");
        alert.setContentText("Wow! You made " + number + " recordings.");

        alert.showAndWait();
    }
}
