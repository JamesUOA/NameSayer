package NameSayerAlpha.models;

import NameSayerAlpha.nameObjects.User;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Record {


    /**
     * recod class handles the recording of audio
     */
    //https://www.codejava.net/coding/capture-and-record-sound-into-wav-file-with-java-sound-api
    // path of the wav file
    private File wavFile;

    // format of audio file
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // the line from which audio data is captured
    private TargetDataLine line;

    public Record (String recordingName) {
        wavFile = new File("Local/" + User.getUser().getName() +"_" + getDateTime() + "_" + recordingName + ".wav");
    }

    /**
     * Defines an audio format
     */
    public AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }

    private String getDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        String name = "0";
        Date date = new Date();
        return formatter.format(date);
    }

    /**
     * Captures the sound and record into a WAV file
     */
    public void start() {
        try {

            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing

            AudioInputStream ais = new AudioInputStream(line);

            // start recording
            AudioSystem.write(ais, fileType, wavFile);

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    public void finish() {
        line.stop();
        line.close();
    }
}
