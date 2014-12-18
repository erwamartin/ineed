package co.erwan.ineed.ineed.Models;

import java.util.ArrayList;

import co.erwan.ineed.ineed.Models.Group;

/**
 * Created by erwanmartin on 16/12/2014.
 */
public class User {

    private String id;

    private String firstname;
    private String picture;

    private ArrayList<Group> selectedGroups;

    public User() {}

    public User(String id, String firstname){
        this.id = id;
        this.firstname = firstname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() { return firstname; }

    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getPicture() { return picture; }

    public void setPicture(String picture) { this.picture = picture; }

    public ArrayList<Group> getSelectedGroups() { return selectedGroups; }

    public void setSelectedGroups(ArrayList<Group> selectedGroups) { this.selectedGroups = selectedGroups; }
}
