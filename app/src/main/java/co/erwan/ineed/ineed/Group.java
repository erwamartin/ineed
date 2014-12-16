package co.erwan.ineed.ineed;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by erwanmartin on 15/12/2014.
 */
public class Group implements Serializable {
    private String id;
    private String name;
    private String coverUrl;
    private ArrayList<User> members;


    public Group(String id, String name){
        this.id = id;
        this.name = name;
        this.coverUrl = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getCoverUrl() { return coverUrl; }

    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public Integer getCountMembers() { return members == null ? 0 : members.size(); }

    public ArrayList<User> getMembers() { return members; }

    public void setMembers(ArrayList<User> members) { this.members = members; }
}

