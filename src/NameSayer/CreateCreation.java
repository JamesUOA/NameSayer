package NameSayer;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * This class handles creating text,media and merging both
 * this is all done on a separate thread so it doesn't block the
 * event dispatch thread
 */
public class CreateCreation extends Thread{

    private String _name;

    public CreateCreation(String name){
        _name=name;
    }

    @Override
    public void run() {
        create();
    }

    /**
     * create first makes the text then creates the media
     * the thread is then slept for 7 seconds to allow processes to execute fully
     * the files are then merged and removed after a short delay
     */
    public void create(){
        try {


            createText();
            createMedia();
            Thread.sleep(5500);
            combineBoth();
            Thread.sleep(1000);
            removeFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * calls the bash command to remove the files after merging
     */
    private void removeFiles(){
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "rm "+ "'" + _name + "'" + ".wav" );
        try {
            Process process = pb.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        ProcessBuilder remove = new ProcessBuilder("/bin/bash", "-c", "rm "+ "'" + _name+"v" + "'" + ".mp4" );
        try {
            Process process = remove.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * calls a bash command to record and create the media file
     */
    private void createMedia(){

        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -loglevel quiet -f pulse -i default -map '0' -t 5 "+ "'" + _name + "'" + ".wav" );
        try {
            Process process = pb.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * command merges the media file and the video file
     */
    private void combineBoth(){

        String cmd = "ffmpeg -loglevel quiet -i "+ "'"+ _name +"v"+"'"+".mp4 -i"+" '"+ _name +"'"+".wav -c:v copy -c:a aac -strict experimental"+" '" + "Creations/"+_name +"'" +".mp4";
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
        try {
            Process process = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * creates the text for the video
     */
    private  void createText(){

        String command = "ffmpeg -loglevel quiet -f lavfi -i color=c=black:s=320x240:d=5 -vf \"  drawtext=/usr/share/fonts/truetype/liberation/ LiberationSans-Regular.ttf:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='";
        String command2 = "'\" ";
        String command3 = ".mp4";
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", command + _name + command2  + "'"+ _name +"v"+"'" + command3 );
        try {
            Process process = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
