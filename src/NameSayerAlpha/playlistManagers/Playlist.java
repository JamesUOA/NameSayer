package NameSayerAlpha.playlistManagers;

import NameSayerAlpha.nameObjects.Name;
import NameSayerAlpha.nameObjects.NameComposite;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Playlist {

    /**
     * playlist class for managing the created playlists
     */

    private URI path;
    private String name;
    private List<Name> names;
    private List<String> namesNotFound;


    public Playlist(String name,URI path){
        namesNotFound = new ArrayList<>();
        names = new ArrayList<>();
        this.name = name;
        this.path = path;
    }

    /**
     * loads the text file and displays the names that weren't found
     */
    public void load(){
        String p = path.toString().substring(path.toString().indexOf('/'));
        File file = new File(p);
        System.out.println();
        Scanner sc = null;

        try {
            sc = new Scanner(file);
            while (sc.hasNextLine()) {
                NameComposite newName = new NameComposite(sc.nextLine());
                if(newName.load()) {
                    names.add(newName);
                }else{
                    namesNotFound.add(newName.toString());

                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String invalidNames = "";
        for(String n: namesNotFound){
            invalidNames+=n+"\n";
        }
        if(namesNotFound.size()!=0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Warning");
            alert.setHeaderText("Name(s) not found in " +name+" playlist");
            alert.setContentText("The following names were not found: " + invalidNames);
            alert.showAndWait();
        }
    }

    public List<Name> getNames() {
        return names;
    }

    @Override
    public String toString(){
        return name;
    }

    public URI getPath() {
        return path;
    }
}
