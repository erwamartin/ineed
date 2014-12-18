package co.erwan.ineed.ineed;

import android.app.Activity;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.parse.Parse;
import com.parse.ParseInstallation;

import co.erwan.ineed.ineed.Models.User;

/**
 * Created by erwanmartin on 18/12/2014.
 */
public class Application extends Activity {

    protected static final String TAG = FacebookConnectFragment.class.getSimpleName();

    protected UserActions userActions;
    protected User currentUser;

    protected RequestQueue mVolleyRequestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVolleyRequestQueue = Volley.newRequestQueue(this);
        mVolleyRequestQueue.start();

        userActions = new UserActions(this);

        currentUser = userActions.getCurrentUser();

        Parse.initialize(this, "xOE5qFgOdWLxtlNO2mfZhvSlBUkjpxjnC8mhexDh", "Qlkb5HnyeBoz35tfrRvHetUxOwfNxRutjEiliOR3");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
