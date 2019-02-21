package NameSayerAlpha.fileManagers;

import NameSayerAlpha.FilePaths;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalNames {

    /**
     * This class manages the local names recordings
     * a singleton pattern was used so every screen had one point
     * of reference and would never go out of sync
     */
    private List<String> namesList;
    private static LocalNames instance;


    private LocalNames() {

        namesList = new ArrayList<>();

        File f = new File(FilePaths.LOCAL_LOCATION);
        File[] matchingFiles = f.listFiles((dir, name) -> name.endsWith(".wav"));

        for (File file : matchingFiles) {
            namesList.add(file.toString().substring(file.toString().indexOf("/")+1));
        }
    }

    /**
     *gets the instance of the singleton
     */
    public static LocalNames getInstance() {
        if(instance==null){
            instance = new LocalNames();
        }
        return instance;
    }

    public List<String> getNamesList() {
        return new ArrayList<String>(namesList);
    }

    /**
     * method for updating the names list whenever a change is made
     */
    public void update() {
        instance = new LocalNames();
    }

}
