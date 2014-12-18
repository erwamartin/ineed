package co.erwan.ineed.ineed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.erwan.ineed.ineed.Helpers.DownloadImageTask;

/**
 * Created by erwanmartin on 18/12/2014.
 */
public class ProfileActivity extends ListNeedsActivity {

    public void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_profile;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        Intent listNeedsActivity = new Intent(getApplicationContext(), ListNeedsActivity.class);
        startActivity(listNeedsActivity);
        finish();
    }
}

