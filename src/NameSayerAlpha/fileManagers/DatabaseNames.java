package NameSayerAlpha.fileManagers;

import NameSayerAlpha.FilePaths;
import NameSayerAlpha.nameObjects.NameVersion;
import NameSayerAlpha.nameObjects.SpecificName;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseNames{

    /**
     * class populates the the list with names in the
     * Database,a singleton pattern was used so every screen had one point
     * of reference and would never go out of sync
     */
    private List<SpecificName> namesList;
    private static DatabaseNames instance;

    /**
     * reads all of the files in the database then sorts it in specific names
     * and then processes the strings an constructs objects
     */
    private DatabaseNames() {

        namesList = new ArrayList<>();

        File f = new File(FilePaths.DATABASE_TRIMMED);
        File[] matchingFiles = f.listFiles((dir, name) -> name.endsWith(".wav"));

        for (File file : matchingFiles) {


                //populating the names list with database names
                String path = file.toString();
                int dataBaseIndex = path.indexOf("\\");
                String fileName = path.substring(dataBaseIndex + 1);

                if (fileName.contains("_") || fileName.contains(".")) {
                    int userIndex = fileName.indexOf("_");
                    int dateTimeIndex = fileName.lastIndexOf("_");
                    int dotIndex = fileName.indexOf(".");

                    String user = fileName.substring(0, userIndex);
                    String dateTime = fileName.substring(userIndex + 1, dateTimeIndex);
                    String name = fileName.substring(dateTimeIndex + 1, dotIndex);
                    //converting name to first upper rest lower case
                    name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

                    boolean nameExists = false;
                    //Checking if name exists
                    for (SpecificName n : namesList) {
                        if (n.equals(name)) {
                            n.addVersion(new NameVersion(user, dateTime, path));
                            nameExists = true;
                        }
                    }

                    //Creates new name if name doesnt exist
                    if (!nameExists) {
                        SpecificName newName = new SpecificName(name, new NameVersion(user, dateTime, path));
                        namesList.add(newName);
                    }

                }
        }
    }

    public static DatabaseNames getInstance() {
        if(instance==null){
            instance = new DatabaseNames();
        }
        return instance;
    }


    public List<SpecificName> getNamesList() {
        return new ArrayList<SpecificName>(namesList);
    }


}