package NameSayerAlpha.nameObjects;

import java.util.List;

public abstract class Name{

    /**
     * name class is abstract and has to be instantiated through Specific name or
     * Name Composite which follows a composite design pattern
     */
    private String name;

    public Name(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }

    public boolean equals(String toCompare){
        if(toCompare.equals(name)){
            return true;
        }
        return false;
    }

    public abstract List<String> getFiles();

    public void setName(String name) {
        this.name = name;
    }

}
