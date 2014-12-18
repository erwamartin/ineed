package co.erwan.ineed.ineed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;


public class ConnectionActivity extends FragmentActivity {

    private FacebookConnectFragment facebookConnectFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle mBundle = intent.getExtras();
        if (mBundle != null) {
            String mData = mBundle.getString("com.parse.Data");
            System.out.println("DATA : xxxxx : " + mData);
            Log.d("GETEXTRAS", "DATA : xxxxx : " + mData);
            try {
                JSONObject jsonData = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                String type = jsonData.getString("type");

                if (type.equals("acceptation")) {
                    String userId = jsonData.getString("user_id");

                    try {
                        getPackageManager().getPackageInfo("com.facebook.katana", 0);
                        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + userId)));
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + userId)));
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + userId)));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            facebookConnectFragment = new FacebookConnectFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, facebookConnectFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            facebookConnectFragment = (FacebookConnectFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
