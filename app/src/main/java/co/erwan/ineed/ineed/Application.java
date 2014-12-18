package co.erwan.ineed.ineed;

import com.parse.Parse;
import com.parse.PushService;

import java.sql.Connection;

/**
 * Created by erwanmartin on 18/12/2014.
 */

public class Application extends android.app.Application {

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the Parse SDK.
        Parse.initialize(this, "xOE5qFgOdWLxtlNO2mfZhvSlBUkjpxjnC8mhexDh", "Qlkb5HnyeBoz35tfrRvHetUxOwfNxRutjEiliOR3");

        // Specify an Activity to handle all pushes by default.
        PushService.setDefaultPushCallback(this, ConnectionActivity.class);
    }
}