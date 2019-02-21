package NameSayerAlpha.nameObjects;

public class User {

    /**
     * class stores info about the current user
     * eg whether they signed in as a guest or with a
     * name, easy for recording names
     */
    private String name;
    private static User user;

    private User(){
    }

    public static User getUser(){
        if(user==null){
            user = new User();
        }
        return user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

}
