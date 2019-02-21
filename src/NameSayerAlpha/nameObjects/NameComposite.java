package NameSayerAlpha.nameObjects;

import NameSayerAlpha.fileManagers.DatabaseNames;

import java.util.ArrayList;
import java.util.List;

public class NameComposite extends Name{

    /**
     * class stores multiple names for example
     * Andrew Hu which is a concatenation of Andrew and Hu
     */

    private List<SpecificName> composition;

    public NameComposite(String name) {
        super(name);
        composition = new ArrayList<>();
    }

    /**
     * gets the name best versions of each name
     * @return
     */
    @Override
    public List<String> getFiles() {
        List<String> files = new ArrayList<>();
        for(SpecificName name:composition){
            files.add(name.getBestVersion().getPath());
        }
        return files;
    }

    //loads the list with names if name is invalid returns false else returns true
    public boolean load(){
        String currentName = this.toString();
        List<SpecificName> databaseList = DatabaseNames.getInstance().getNamesList();
        String[] individualNames = currentName.split("\\s+");

        for(String name: individualNames){
            boolean nameExists = false;
            for(SpecificName n: databaseList){
                if(n.equals(name)){
                    composition.add(n);
                    nameExists = true;
                }
            }
            if(!nameExists){
                return false;
            }
        }
        return true;
    }

    public List<Name> getComposition() {
        return new ArrayList<>(composition);
    }
}
