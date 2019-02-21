package NameSayerAlpha;

import NameSayerAlpha.ManageControllers.ManageControllers;
import NameSayerAlpha.fileManagers.LocalNames;
import NameSayerAlpha.models.Achievement;
import NameSayerAlpha.models.Record;
import NameSayerAlpha.nameObjects.Name;
import NameSayerAlpha.nameObjects.NameVersion;
import NameSayerAlpha.nameObjects.SpecificName;
import NameSayerAlpha.playlistManagers.CurrentPlaylist;
import com.jfoenix.controls.JFXListView;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RecordScreenController {

    @FXML private ProgressBar micLevel;
    @FXML private Button recordButton;
    @FXML private Button badQualityButton;
    @FXML private Label badQualityLabel;
    @FXML private ProgressIndicator recordIndicator;
    @FXML private JFXListView playlistNames;
    @FXML private JFXListView localNames;
    @FXML private Label recordingLabel;
    @FXML private Label selectedName;

    private boolean isRecording = true;
    private int _recordings;
    private final String checkedMark = "\u2713";
    private String _selectedName;
    private Achievement achievement;
    private Record record;
    private ObservableList localNamesValues;
    private static Thread recorder;
    private Image startRecordingImage;
    private Image stopRecordingImage;



    public void initialize() {
        databaseNamesSetUp();
        localNamesSetUp();
        micLevel.setStyle("-fx-accent: #ff4f17;");
        setUpMic();
        imageViewSetUp();
        //Creates a new achievement obj when page is started up.
        achievement = new Achievement();
    }

    public void databaseNamesSetUp() {
        playlistNames.setItems(CurrentPlaylist.getInstance().getNamesList());
    }

    public void localNamesSetUp() {
        localNamesValues = FXCollections.observableArrayList(LocalNames.getInstance().getNamesList());
        localNames.setItems(localNamesValues);
    }

    public void imageViewSetUp() {

        startRecordingImage = new Image(new File(FilePaths.RECORD_ON).getPath());
        stopRecordingImage = new Image(new File(FilePaths.RECORD_OFF).getPath());

    }

    private void setUpMic() {
        //Starts the mic volume level on a a new thread.
        new Thread(() -> {
            //https://stackoverflow.com/questions/15870666/calculating-microphone-volume-trying-to-find-max/15872655#15872655
            TargetDataLine line = null;
            AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("The line is not supported.");
            }
            // Obtain and open the line.
            try {
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();
            } catch (LineUnavailableException ex) {
                System.out.println(ex);
            }

            while (true) {
                byte[] bytes = new byte[line.getBufferSize() / 5];
                line.read(bytes, 0, bytes.length);
                //System.out.println("RMS Level: " + calculateRMSLevel(bytes));
                double output = (double) calculateRMSLevel(bytes) / 100;
                micLevel.setProgress(output);
            }
        }).start();
    }

    //https://stackoverflow.com/questions/15870666/calculating-microphone-volume-trying-to-find-max/15872655#15872655
    //Method to calculate the RMS of the input.
    public int calculateRMSLevel(byte[] audioData) {
        // audioData might be buffered data read from a data line
        long lSum = 0;
        for (int i = 0; i < audioData.length; i++)
            lSum = lSum + audioData[i];

        double dAvg = lSum / audioData.length;

        double sumMeanSquare = 0d;
        for (int j = 0; j < audioData.length; j++)
            sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

        double averageMeanSquare = sumMeanSquare / audioData.length;
        return (int) (Math.pow(averageMeanSquare, 0.5d) + 0.5);

    }



    public void recordButtonClicked() {
        //Returns e.g. 21-09-2018_11-19-17
        //Gets the current time and date for the creation of a new .wav file

        if(playlistNames.getSelectionModel().getSelectedItem()!=null) {
            if (isRecording) {
                //When the record button is pressed then it will increment the number of recordings made.
                achievement.add();
                recordButton.setGraphic(new ImageView(stopRecordingImage));
                recordingLabel.setText("Recording...");
                String fileName = _selectedName.replace(" ", "_");
                recorder = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //gets the Selected name from the database list view to create a recording. s
                        record = new Record(fileName);
                        record.start();
                    }
                });

                Thread stopper = new Thread(new Runnable() {
                    public void run() {
                        try {
                            //Gives user 20seconds to record.
                            Thread.sleep(20000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        record.finish();
                    }
                });

                stopper.start();
                recorder.start();
                isRecording = false;
            } else {
                recordButton.setGraphic(new ImageView(startRecordingImage));
                recordingLabel.setText("Done" + checkedMark);
                record.finish();
                recorder.stop();
                isRecording = true;

                LocalNames.getInstance().update();
                localNamesSetUp();

                //Waits for 2 seconds before removing the label.
                PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1));
                pauseTransition.setOnFinished(e -> recordingLabel.setText("Idle"));
                pauseTransition.play();

                //This checks the number of recordings made then outputs the dialog.
                _recordings = achievement.checkNumberOfRecordings();
                Platform.runLater(() -> achievement.displayAchievement(_recordings));
            }
        }
    }



    public void onNameSelect(){
        if(playlistNames.getSelectionModel().getSelectedItem()!=null) {
            _selectedName = playlistNames.getSelectionModel().getSelectedItem().toString();
            selectedName.setText(_selectedName);
            String currentPlaylist = CurrentPlaylist.getInstance().getPlaylist().toString();
            String currentName = playlistNames.getSelectionModel().getSelectedItem().toString();
            ManageControllers.getInstance().getFrame().setSelection(currentName, currentPlaylist);
            ManageControllers.getInstance().getFrame().clearQueue();
            ManageControllers.getInstance().getFrame().addToQueue((Name)playlistNames.getSelectionModel().getSelectedItem());
        }
    }

    public void onCustomName() {
        if (localNames.getSelectionModel().getSelectedItems() != null) {
            ManageControllers.getInstance().getFrame().setSelection("User Recording", "-");
            ManageControllers.getInstance().getFrame().clearQueue();
            String name = localNames.getSelectionModel().getSelectedItem().toString();
            ManageControllers.getInstance().getFrame().addToQueue((new SpecificName(name, new NameVersion("", "", "Local/" + name))));
        }
    }


    @FXML
    public void deleteRecording(){

        if(localNames.getSelectionModel().getSelectedItem()!=null) {
            String fileName = localNames.getSelectionModel().getSelectedItem().toString();
            localNamesValues.remove(localNames.getSelectionModel().getSelectedItem());
            File file = new File(FilePaths.LOCAL_LOCATION + "/" + fileName);
            try {
                Files.deleteIfExists(file.toPath());
                ManageControllers.getInstance().getFrame().clearQueue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}