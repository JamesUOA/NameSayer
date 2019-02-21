package NameSayerAlpha.nameObjects;

public class NameVersion {

    /**
     * the specific versions of a name is stored in this class
     * includes User who created, dateTime and path to the version
     */
    private String user;
    private String dateTime;
    private String path;

    public NameVersion(String user, String dateTime,String path){
        this.user = user;
        this.dateTime = dateTime;
        this.path =path;
    }

    public String getUser() {
        return user;
    }

    public String getPath() {
        return path;
    }

    public String toString(){
        return path.substring(path.indexOf("/")+1);
    }

    public String getDateTime() {
        return dateTime;
    }
}
