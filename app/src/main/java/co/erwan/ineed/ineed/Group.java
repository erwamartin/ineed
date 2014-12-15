package co.erwan.ineed.ineed;

import java.io.Serializable;

/**
 * Created by erwanmartin on 15/12/2014.
 */
public class Group implements Serializable {
    private Integer id;
    private String name;


    public Group(int id, String name){
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Integer getCountMembers() { return 20; }
}

