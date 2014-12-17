package co.erwan.ineed.ineed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.erwan.ineed.ineed.Helpers.DownloadImageTask;

/**
 * Created by erwanmartin on 16/12/2014.
 */
public class ListNeedsActivity extends Activity {

    private static final String TAG = MainFragment.class.getSimpleName();

    private ArrayList<Group> groups;
    private HashMap<Group, List<Need>> needs;
    private GroupNeedListAdapter groupsAdapter;
    private ExpandableListView groupsList;
    private UserActions userActions;
    private User currentUser;

    private RequestQueue mVolleyRequestQueue;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        groups = new ArrayList<Group>();
        needs = new HashMap<Group, List<Need>>();

        mVolleyRequestQueue = Volley.newRequestQueue(this);
        mVolleyRequestQueue.start();

        userActions = new UserActions(this);

        setContentView(R.layout.activity_list_needs);
        groupsList = (ExpandableListView) findViewById(R.id.groupsList);

        currentUser = userActions.getCurrentUser();
        //groups = currentUser.getSelectedGroups();

        getNeedsAPI();

        TextView user_firstname = (TextView) findViewById(R.id.user_firstname);
        user_firstname.setText(currentUser.getFirstname());

        new DownloadImageTask((ImageView) findViewById(R.id.user_picture))
                .execute(currentUser.getPicture());

        ImageButton addNeedButton = (ImageButton)findViewById(R.id.add_need_button);
        addNeedButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent addNeedActivity = new Intent(ListNeedsActivity.this, AddNeedActivity.class);
                startActivity(addNeedActivity);
            }

        });

    }

    private void getNeedsAPI () {
        String url = this.getResources().getString(R.string.server_path) + this.getResources().getString(R.string.get_user_needs);
        url = url.replace("{user_id}", currentUser.getId().toString());

        JsonArrayRequest createUserRequest = new JsonArrayRequest(url, new com.android.volley.Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                // TODO Auto-generated method stub
                Log.d("getUser", response.toString());
                if (response.length() == 0) {
                    Log.d("createUser", "EXISTE PAS");

                    Intent selectGroupsActivity = new Intent(getApplicationContext(), SelectGroupsActivity.class);
                    startActivity(selectGroupsActivity);

                } else {
                    Log.d("getNeedsAPI", "EXISTE");

                    groups.clear();
                    needs = new HashMap<Group, List<Need>>();

                    for (int i = 0, leni = response.length(); i < leni; i++) {
                        try {
                            JSONObject jsonGroup = (JSONObject)response.get(i);
                            Group newGroup = new Group(jsonGroup.getString("fbGroupId"), jsonGroup.getString("name"));
                            groups.add(newGroup);

                            JSONArray jsonNeeds = (JSONArray)jsonGroup.getJSONArray("items");

                            List<Need> needList = new ArrayList<Need>();

                            for (int j = 0, lenj = jsonNeeds.length(); j < lenj; j++) {
                                JSONObject jsonNeed = (JSONObject)jsonNeeds.get(j);

                                User user = new User();
                                user.setFirstname(jsonNeed.getString("firstname"));
                                user.setPicture(jsonNeed.getString("picture"));

                                Need newNeed = new Need(jsonNeed.getString("idItem"), jsonNeed.getString("message"), user);
                                needList.add(newNeed);
                            }

                            needs.put(newGroup, needList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (groupsAdapter == null) {
                       Log.d("ROUPADAPTER", "groupsAdapter == null");
                        groupsAdapter = new GroupNeedListAdapter(ListNeedsActivity.this, groups, needs);
                        groupsList.setAdapter(groupsAdapter);
                    } else {
                        Log.d("ROUPADAPTER", "groupsAdapter != null");
                        ((GroupNeedListAdapter) groupsAdapter).notifyDataSetChanged();

                    }

                    currentUser.setSelectedGroups(groups);
                    userActions.setCurrentUser(currentUser);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d("createUser", error.toString());
            }
        });

        mVolleyRequestQueue.add(createUserRequest);

    }

    public void removeNeed (final Group group, final Need need) {

        String url = this.getResources().getString(R.string.server_path) + this.getResources().getString(R.string.remove_need);
        url = url.replace("{need_id}", need.getId());

        StringRequest removeNeedRequest = new StringRequest(com.android.volley.Request.Method.DELETE, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                Toast.makeText(ListNeedsActivity.this, "Supprimé", Toast.LENGTH_LONG).show();

                needs.get(group).remove(need);

                ((GroupNeedListAdapter) groupsAdapter).notifyDataSetChanged();
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d("removeNeedRequest", error.toString());
            }
        });

        mVolleyRequestQueue.add(removeNeedRequest);
    }
}

