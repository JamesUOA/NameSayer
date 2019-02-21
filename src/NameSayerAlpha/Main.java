package NameSayerAlpha;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main extends Application {

    LoadingController l;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FilePaths.LOADING_FXML));
        Parent root = loader.load();
        primaryStage.setTitle("Name Sayer");
        primaryStage.setScene(new Scene(root, 1366, 768));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.show();

        l = loader.getController();
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                Platform.runLater(()->l.setTask("normalizing audio..."));
                normalize();
                Platform.runLater(()->l.setTask("trimming audio..."));
                trim();
                return null;
            }
        };
        task.setOnSucceeded((e)-> {
            Parent mainMenu = null;
            try {
                mainMenu = FXMLLoader.load(getClass().getResource(FilePaths.MAINMENU_FXML));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            primaryStage.setTitle("Name Sayer");
            primaryStage.setScene(new Scene(mainMenu, 1366, 768));
                }
        );
        new Thread(task).start();
    }

    /**
     * Trimming the audio on startup
     */
    public void trim(){
        File index = new File(FilePaths.DATABASE_TRIMMED);
        String[]entries = index.list();
        if(entries!=null) {
            for (String s : entries) {
                File currentFile = new File(index.getPath(), s);
                currentFile.delete();
            }
        }
        index.delete();
        File newFile = new File(FilePaths.DATABASE_TRIMMED);
        newFile.mkdir();
        File f = new File(FilePaths.DATABASE_NORMALIZED);
        File[] matchingFiles = f.listFiles((dir, name) -> name.endsWith(".wav"));

        for (File file : matchingFiles) {
            Platform.runLater(()->l.setFile(file.getName()));
            //populating the names list with database names
            String cmd = "ffmpeg -i '"+FilePaths.DATABASE_NORMALIZED+"/" + file.getName()+ "' -af silenceremove=1:0:-50dB"+ " ./"+FilePaths.DATABASE_TRIMMED+"/" + file.getName();
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash","-c",cmd);
            try {
                processBuilder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * normalizing the audio
     */
    public void normalize() {

        int targetVolume = -35;
        File index = new File(FilePaths.DATABASE_NORMALIZED);
        String[]entries = index.list();
        if(entries!=null) {
            for (String s : entries) {
                File currentFile = new File(index.getPath(), s);
                currentFile.delete();
            }
        }
        index.delete();
        File f = new File(FilePaths.DATABASE_LOCATION);
        File[] matchingFiles = f.listFiles((dir, name) -> name.endsWith(".wav"));
        File newFile = new File(FilePaths.DATABASE_NORMALIZED);
        newFile.mkdir();
        for (File file : matchingFiles) {

            Platform.runLater(()->l.setFile(file.getName()));
            try {
                //extract the mean volume from the audio file using ffmpeg
                String cmd1 = "ffmpeg -i '" +FilePaths.DATABASE_LOCATION+"/" + file.getName()+ "' -filter:a volumedetect -f null /dev/null 2>&1| grep mean_volume";
                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd1);
                Process volume = builder.start();
                volume.waitFor();

                //read in mean volume from stdout
                BufferedReader br = new BufferedReader(new InputStreamReader(volume.getInputStream()));
                String output = br.readLine();

                //calculate the difference between target volume and extracted volume
                int originalVolume = Integer.valueOf(output.substring(output.lastIndexOf(':') + 2, output.lastIndexOf('.')));
                int difference = targetVolume - originalVolume;

                //normalise the audio by increasing or decreasing by the difference
                String cmd2 = "ffmpeg -i '"+FilePaths.DATABASE_LOCATION+"/" + file.getName()+"' -filter:a \"volume=" + difference + "dB\" "+FilePaths.DATABASE_NORMALIZED+"/"+file.getName();
                ProcessBuilder builder2 = new ProcessBuilder("/bin/bash", "-c", cmd2);
                Process normalise = builder2.start();
                normalise.waitFor();

            } catch (IOException | InterruptedException e) {
                System.out.println(e);
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }



}
