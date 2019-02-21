package NameSayerAlpha.nameObjects;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SpecificName extends Name{

    /**
     * specific Name stores the Versions of each name and
     * the best version of the names
     */

    private List<NameVersion> versions;
    private NameVersion bestVersion;

    public SpecificName(String name, NameVersion version) {
        super(name);
        this.versions = new ArrayList<>();
        versions.add(version);
        bestVersion = version;
    }



    /**
     * adds a version of the name
     * @param version
     */
    public void addVersion(NameVersion version){
        versions.add(version);

        File file = new File("BadQuality.txt");
        String bestName = bestVersion.getPath();
        bestName = bestName.substring(bestName.indexOf("/")+1);
        try {
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                if(scanner.nextLine().equals(bestName)){
                    bestVersion = version;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * gets the best version of name(returns a String)
     * @return
     */
    @Override
    public List<String> getFiles() {
        List<String> files = new ArrayList<String>();
        files.add(bestVersion.getPath());
        return files;
    }

    public List<NameVersion> getVersions() {
        return versions;
    }

    public NameVersion getBestVersion(){
        return bestVersion;
    }

}
