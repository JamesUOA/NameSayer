package NameSayerAlpha.models;

import javafx.concurrent.Task;
import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class SoundPlayer extends Task<Void> {

    //https://alvinalexander.com/java/java-audio-example-java-au-play-sound
    private List<String> path;
    private AudioStream audioStream;
    private double totalDuration;
    private boolean currentlyPlaying = false;
    private boolean stopped;

    public SoundPlayer(List<String> path){
        totalDuration = 0;
        this.path = path;
        stopped = false;
        setTotalDuration();
    }

    /**
     * calculates the total duration of the specified files and
     * stores in in the field
     */
    public void setTotalDuration(){
        for(String f: path){
            totalDuration+=getLength(f);
        }

    }

    /**
     * gets the length of the specified file and returns the length in double
     * @param file
     * @return length
     */
    public double getLength(String file){
        AudioInputStream audioInputStream = null;
        double durationInSeconds=0;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new File(file));
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();

            durationInSeconds = (frames) / format.getFrameRate();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return durationInSeconds;
    }


    public void stopAudio() {
        if (audioStream != null) {
            if (isPlaying()) {
                AudioPlayer.player.stop(audioStream);
                currentlyPlaying = false;
                stopped = true;
            }
        }
    }

    /**
     * method for playing the files specified
     */
    public void playAudio() {
        currentlyPlaying = true;
        new Thread(() -> {
            this.updateProgress(0, totalDuration * 1000);
            for (int i = 0; i < (int) (totalDuration * 1000); i += 1) {
                if (currentlyPlaying) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateProgress(i, totalDuration * 1000);
                } else {
                    break;
                }
            }
            this.updateProgress(0, totalDuration * 1000);
        }).start();


        //Resets the progressBar to 0
        for (String p : path) {
            if (!stopped) {
                try {
                    InputStream in = new FileInputStream(new File(p));
                    audioStream = new AudioStream(in);
                    AudioPlayer.player.start(audioStream);
                    currentlyPlaying = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep((long) (getLength(p) * 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }

        }
    }

    /**
     * checks for if the audio player is currently playing audio
     * @return
     */
    public boolean isPlaying() {
       return AudioPlayer.player.isAlive();
    }

    @Override
    protected Void call() throws Exception {
        playAudio();
        return null;
    }
}
