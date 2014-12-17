package co.erwan.ineed.ineed;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by erwanmartin on 17/12/2014.
 */
public class Need implements Serializable {
    private String id;
    private String content;
    private User user;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public Need(String id, String content, User user) {
        this.id = id;
        this.content = content;
        this.user = user;
    }
}

