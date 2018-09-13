package NameSayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;


/**
 * this class is dedicated to returning a list of the current creations
 * creating an instance of this class creates an instance of all the .mp4 files in the working directory
 * then the get creations method returns this list
 */
public class ModelManage {

    ObservableList<String> _creations;


    public ModelManage(){
        _creations = FXCollections.observableArrayList();

        String cmd =  "ls Creations | grep .mp4";
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);

        try {

            Process process = pb.start();
            BufferedReader stdoutBuffered =new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = null;
            while ((line = stdoutBuffered.readLine()) != null ) {
                _creations.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> get_creations(){
        return _creations;
    }


}
