package co.erwan.ineed.ineed;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import co.erwan.ineed.ineed.Models.User;

/**
 * Created by erwanmartin on 16/12/2014.
 */
public class UserActions {

    private Context context;

    public UserActions(Context context) {
        this.context = context;
    }

    protected void setCurrentUser(User user) {
        SharedPreferences sharedPref = this.context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPref.edit();

        Gson gson = new Gson();
        String json_user = gson.toJson(user);
        prefsEditor.putString("currentUser", json_user);
        prefsEditor.commit();
    }

    public User getCurrentUser() {
        SharedPreferences sharedPref = this.context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json_user = sharedPref.getString("currentUser", "");
        User user = gson.fromJson(json_user, User.class);

        return user;
    }

}
