package co.erwan.ineed.ineed.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by erwanmartin on 15/12/2014.
 */
public class Group implements Serializable {
    private String id;
    private String name;
    private ArrayList<User> members;
    private Boolean selected;


    public Group(String id, String name){
        this.id = id;
        this.name = name;
        this.selected = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Boolean getSelected() { return selected; }

    public void setSelected(Boolean selected) { this.selected = selected; }

    public Integer getCountMembers() { return members == null ? 0 : members.size(); }

    public ArrayList<User> getMembers() { return members; }

    public void setMembers(ArrayList<User> members) { this.members = members; }
}

