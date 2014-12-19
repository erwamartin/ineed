package co.erwan.ineed.ineed;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by erwanmartin on 18/12/2014.
 */

public class Receiver extends ParsePushBroadcastReceiver {

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Intent i = new Intent(context, ConnectionActivity.class);

        Log.d("intent.getExtras()", intent.getExtras().toString());
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}