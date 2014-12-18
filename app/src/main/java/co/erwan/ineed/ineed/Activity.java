package co.erwan.ineed.ineed;

import android.content.Context;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.parse.Parse;
import com.parse.ParseInstallation;

import co.erwan.ineed.ineed.Models.User;

/**
 * Created by erwanmartin on 18/12/2014.
 */
public class Activity extends android.app.Activity {

    protected static final String TAG = FacebookConnectFragment.class.getSimpleName();
    private static Activity instance = new Activity();

    protected UserActions userActions;
    protected User currentUser;

    protected RequestQueue mVolleyRequestQueue;

    public Activity() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVolleyRequestQueue = Volley.newRequestQueue(this);
        mVolleyRequestQueue.start();

        userActions = new UserActions(this);

        currentUser = userActions.getCurrentUser();
    }
}
