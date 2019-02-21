package NameSayerAlpha;

import NameSayerAlpha.ManageControllers.ManageControllers;
import NameSayerAlpha.models.SoundPlayer;
import NameSayerAlpha.nameObjects.Name;
import NameSayerAlpha.playlistManagers.CurrentPlaylist;
import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.*;

public class FrameController {

    @FXML private Button playButton;
    @FXML private Button shuffleButton;
    @FXML private Button repeatButton;
    @FXML private Button exitButton;
    @FXML private Button badQualityButton;
    @FXML private Label badQualityLabel;
    @FXML private ProgressBar playerProgress;
    @FXML private Label currentName;
    @FXML private Label playlist;
    @FXML private Label firstCharacter;
    @FXML private TabPane testing;
    @FXML private Slider volumeSlider;
    @FXML private HBox topBar;
    
    private boolean repeatButtonPressed = false; //When this is True, then the image will be yellow.
    private boolean shuffleButtonPressed = false;//When this is True, then the image will be yellow.
    private boolean isPlayButton = true;
    private final String checkedMark = "\u2713";
    private Image stopImage;
    private Image playImage;
    private SoundPlayer player;
    private List<Name> playQueue;//Field which handles the repeated playing of names
    private List<String> fileQueue;//Field which handles a name with multiple files
    private double xOffset = 0;
    private double yOffset = 0;



    public void initialize() {
        playQueue = new ArrayList<>();
        imageViewSetUp();
        loadTabs();
        startVolumeSlider();
        topBarSetup();
    }

    /**
     * Method Loads all the tabs that are in the tab pane,
     */
    public void loadTabs(){

        Parent root = null;
        Parent playlist = null;
        Parent browse = null;
        Parent practice = null;
        FXMLLoader practiceLoader = new FXMLLoader(getClass().getResource(FilePaths.PRACTICE_FXML));
        FXMLLoader browseLoader = new FXMLLoader(getClass().getResource(FilePaths.BROWSE_FXML));
        FXMLLoader playlistLoader = new FXMLLoader(getClass().getResource(FilePaths.PLAYLIST_FXML));
        FXMLLoader playingLoader = new FXMLLoader(getClass().getResource(FilePaths.PLAYING_FXML));
        try {
            root = playingLoader.load();
            browse = browseLoader.load();
            playlist = playlistLoader.load();
            practice = practiceLoader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
        ManageControllers.getInstance().setPlayingController(playingLoader.getController());
        ManageControllers.getInstance().setBrowse(browseLoader.getController());
        ManageControllers.getInstance().setPractice(practiceLoader.getController());
        ManageControllers.getInstance().setPlaylist(playlistLoader.getController());
        testing.getTabs().get(0).setContent(root);
        testing.getTabs().get(1).setContent(browse);
        testing.getTabs().get(2).setContent(playlist);
        testing.getTabs().get(3).setContent(practice);
    }


    /**
     * sets up the top bar so the entire screen is moveable
     */
    public void topBarSetup(){

        topBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        //move around here
        topBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage)exitButton.getScene().getWindow();
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
    }

    /**
     * sets up images with references to the images
     */
    public void imageViewSetUp() {

        stopImage = new Image(new File(FilePaths.STOP_BUTTON).getPath());
        playImage = new Image(new File(FilePaths.PLAY_BUTTON).getPath());
    }


    /**
     * When the bad quality button is clicked, it first checks fro special cases then appends the file to
     * BadQuality.txt
     */
    public void badQualityButtonClicked() {

        String currentFile = null;
        if(fileQueue!=null) {
            if (fileQueue.size() == 1) {
                currentFile = fileQueue.get(0);
            } else if (fileQueue.size() > 1) {
                badQualityLabel.setText("Select One File");
            }
            if (currentFile != null) {
                String badName = currentFile.substring(currentFile.indexOf("/") + 1);
                if (badName != null) {
                    File file = new File("BadQuality.txt");
                    boolean lineExists = false;
                    try {
                        Scanner scanner = new Scanner(file);
                        //now read the file line by line...
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            if (line.contains(badName)) {
                                //if the name already exists, then output this text.
                                badQualityLabel.setText("In file");
                                lineExists = true;
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (!lineExists) {

                        //Adding the bad quality people to the text.
                        //System.out.println("Placed into the file.");
                        ProcessBuilder process = new ProcessBuilder("/bin/bash", "-c", "echo " + "'" + badName + "'" + " >> " + "BadQuality.txt");
                        try {
                            process.start();
                            badQualityLabel.setText("Done " + checkedMark);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            //Waits for 2 seconds before removing the label.
            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
            pauseTransition.setOnFinished(e -> badQualityLabel.setText(""));
            pauseTransition.play();
        }
    }


    /**
     * The play button switches between play and stop
     */
    public void playButtonClicked() {
        //Play and stop buttons switch between each other so need check
        if(isPlayButton){
            playButton();
        }else{
            stopButton();
        }
    }

    /**
     * the functionality of the play button is extracted into this method
     */
    public void playButton(){
        Name playName;
        //checks if the queue is empty
        if(!playQueue.isEmpty()) {
            playName = playQueue.get(0);
            fileQueue = playName.getFiles();
            //if the repeat button is pressed
            if(repeatButtonPressed){
                playQueue.add(playQueue.get(0));
            }
            if(CurrentPlaylist.getInstance().getPlaylist()!=null) {
                setSelection(playQueue.get(0).toString(), CurrentPlaylist.getInstance().getPlaylist().toString());
            }else{
                setSelection(playQueue.get(0).toString(),"-");
            }
            if(fileQueue!=null) {
                playButton.setGraphic(new ImageView(stopImage));
                //Now the playButton changed into a stopButton.
                isPlayButton = false;
                player = new SoundPlayer(fileQueue);
                playerProgress.progressProperty().unbind();
                playerProgress.progressProperty().bind(player.progressProperty());
                //Run only after the audio is played.
                player.setOnSucceeded(e -> {
                    playButton.setGraphic(new ImageView(playImage));
                    isPlayButton = true;

                    if(playQueue.size()>1) {
                        playQueue.remove(0);
                        playButtonClicked();
                    }
                });
                new Thread(player).start();

            }
        }
    }

    /**
     * checks if shuffle is currently selected
     * @return
     */
    public boolean isShuffle(){
        return shuffleButtonPressed;
    }

    /**
     * shuffles the playlist
     */
    public void shufflePlaylist(){
        Collections.shuffle(playQueue);
    }


    /**
     * when the stop button is pressed clear queue and append the most recent
     * file played
     */
    private void stopButton() {
        if(repeatButtonPressed) {
            repeatButtonClicked();
        }
        Name n = playQueue.get(0);
        playQueue.clear();
        playQueue.add(n);
        player.stopAudio();
    }


    /**
     * when shuffle is
     */
    public void shuffleButtonClicked() {
        if (!shuffleButtonPressed) {
            Image image = new Image(new File(FilePaths.SHUFFLE_ON).getPath());
            shuffleButton.setGraphic(new ImageView(image));
            shuffleButtonPressed = true;

        } else {
            Image image = new Image(new File(FilePaths.SHUFFLE_OFF).getPath());
            shuffleButton.setGraphic(new ImageView(image));
            shuffleButtonPressed = false;
        }
    }

    public void repeatButtonClicked() {
        if (!repeatButtonPressed) {
            Image image = new Image(new File(FilePaths.REPEAT_ON).getPath());
            repeatButton.setGraphic(new ImageView(image));
            repeatButtonPressed = true;
        } else {
            Image image = new Image(new File(FilePaths.REPEAT_OFF).getPath());
            repeatButton.setGraphic(new ImageView(image));
            repeatButtonPressed = false;
        }
    }


    /**
     * adds a file to the queue
     * @param newName
     */
    public void addToQueue(Name newName){
        playQueue.add(newName);
    }

    public void clearQueue(){
        playQueue.clear();
    }

    /**
     * sets the file and playlist displays on the bottom left side of the frame
     * @param selection
     * @param playlist
     */
    public void setSelection(String selection ,String playlist){
        firstCharacter.setText(selection.substring(0,1));
        this.currentName.setText(selection);
        this.playlist.setText(playlist);
        if(!selection.equals("User Recording")) {
            ManageControllers.getInstance().getPlayingController().setContent(playlist,selection);
        }
    }



    //https://www.youtube.com/watch?v=X9mEBGXX3dA reference
    private void startVolumeSlider(){

        String cmd1 = "amixer get Master | awk '$0~/%/{print $4}' | tr -d '[]%'";
        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd1);

        try {
            Process volumeInitializer = builder.start();
            InputStream inputStream = volumeInitializer.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String volumeLevel = br.readLine();
            double vlevel = Double.parseDouble(volumeLevel);
            volumeSlider.setValue(vlevel);

        } catch (IOException e) {
            e.printStackTrace();
        }

        volumeSlider.valueProperty().addListener(observable -> {
            double volume = volumeSlider.getValue();
            String cmd2 = "amixer set 'Master' " + volume + "%";
            ProcessBuilder builder1 = new ProcessBuilder("/bin/bash", "-c", cmd2);
            try {
                builder1.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void exitButtonClicked() {
        System.exit(0);
    }
}