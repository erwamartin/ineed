package co.erwan.ineed.ineed;

import java.util.ArrayList;

/**
 * Created by erwanmartin on 16/12/2014.
 */
public class User {

    private Long id;
    private String name;

    private String firstname;
    private String picture;

    private ArrayList<Group> selectedGroups;

    public User(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getFirstname() { return firstname; }

    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getPicture() { return picture; }

    public void setPicture(String picture) { this.picture = picture; }

    public ArrayList<Group> getSelectedGroups() { return selectedGroups; }

    public void setSelectedGroups(ArrayList<Group> selectedGroups) { this.selectedGroups = selectedGroups; }
}
