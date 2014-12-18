package co.erwan.ineed.ineed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.MotionEvent;
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
import com.parse.ParsePush;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.erwan.ineed.ineed.Adapters.GroupNeedListAdapter;
import co.erwan.ineed.ineed.Helpers.DownloadImageTask;
import co.erwan.ineed.ineed.Models.Group;
import co.erwan.ineed.ineed.Models.Need;
import co.erwan.ineed.ineed.Models.User;

/**
 * Created by erwanmartin on 16/12/2014.
 */
public class ListNeedsActivity extends Application implements SwipeRefreshLayout.OnRefreshListener {

    protected Integer layout = R.layout.activity_list_needs;

    protected ArrayList<Group> groups;
    protected HashMap<Group, List<Need>> needs;
    protected GroupNeedListAdapter groupsAdapter;
    protected ExpandableListView groupsList;
    protected SwipeRefreshLayout swipeLayout;

    protected ImageButton addNeedButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        groups = new ArrayList<Group>();
        needs = new HashMap<Group, List<Need>>();

        setContentView(layout);
        groupsList = (ExpandableListView) findViewById(R.id.groupsList);

        getNeedsAPI();

        TextView user_firstname = (TextView) findViewById(R.id.user_firstname);
        user_firstname.setText(currentUser.getFirstname());

        ImageView userPicture = (ImageView) findViewById(R.id.user_picture);

        new DownloadImageTask(userPicture)
                .execute(currentUser.getPicture());

        addNeedButton = (ImageButton)findViewById(R.id.add_need_button);
        addNeedButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent addNeedActivity = new Intent(ListNeedsActivity.this, AddNeedActivity.class);
                startActivity(addNeedActivity);
            }

        });

        if(this.getLocalClassName().equals("ListNeedsActivity")) {
            userPicture.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    goToProfile();
                }
            });

            user_firstname.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    goToProfile();
                }
            });

            TextView edit_needs = (TextView) findViewById(R.id.edit_needs);
            edit_needs.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    goToProfile();
                }
            });

            swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            swipeLayout.setOnRefreshListener(this);
        }

    }

    @Override
    public void onRefresh() {
        /*new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 5000);*/
        getNeedsAPI();
    }


    protected void goToProfile() {
        Intent profileActivity = new Intent(ListNeedsActivity.this, ProfileActivity.class);
        startActivity(profileActivity);
    }

    protected void getNeedsAPI () {
        String url = this.getResources().getString(R.string.server_path) + this.getResources().getString(R.string.get_user_needs);
        url = url.replace("{user_id}", currentUser.getId().toString());

        final ListNeedsActivity _this = this;

        Log.d("getNeedsAPI", _this.getLocalClassName());

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
                            newGroup.setCountMembers(jsonGroup.getInt("nbUser"));
                            groups.add(newGroup);

                            JSONArray jsonNeeds = (JSONArray)jsonGroup.getJSONArray("items");

                            List<Need> needList = new ArrayList<Need>();

                            for (int j = 0, lenj = jsonNeeds.length(); j < lenj; j++) {
                                JSONObject jsonNeed = (JSONObject)jsonNeeds.get(j);

                                Log.d("_this.getLocalClassName()", _this.getLocalClassName());

                                if(_this.getLocalClassName().equals("ListNeedsActivity") || jsonNeed.getString("idFb").equals(currentUser.getId().toString())) {
                                    User user = new User(jsonNeed.getString("idFb"), jsonNeed.getString("firstname"));
                                    user.setPicture(jsonNeed.getString("picture"));

                                    Need newNeed = new Need(jsonNeed.getString("idItem"), jsonNeed.getString("message"), user);
                                    needList.add(newNeed);
                                }
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
                        groupsAdapter = new GroupNeedListAdapter(ListNeedsActivity.this, groups, needs);
                        groupsList.setAdapter(groupsAdapter);
                        ((GroupNeedListAdapter) groupsAdapter).notifyDataSetChanged();

                    }

                    for(int i=0, len = groupsAdapter.getGroupCount(); i < len ; i++)
                        groupsList.expandGroup(i);

                    currentUser.setSelectedGroups(groups);
                    userActions.setCurrentUser(currentUser);

                    if(_this.getLocalClassName().equals("ListNeedsActivity")) {
                        swipeLayout.setRefreshing(false);
                    }
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

    public void removeNeed (final Group group, final Need need, final View view) {

        String url = this.getResources().getString(R.string.server_path) + this.getResources().getString(R.string.remove_need);
        url = url.replace("{need_id}", need.getId());

        StringRequest removeNeedRequest = new StringRequest(com.android.volley.Request.Method.DELETE, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response

                Log.d("needUser", currentUser.getFirstname().toString());
                Log.d("needUser", need.getUser().getFirstname().toString());

                if (need.getUser().getId().toString().equals(currentUser.getId().toString())) {
                    Toast.makeText(ListNeedsActivity.this, "Annonce supprimée", Toast.LENGTH_LONG).show();
                } else {

                    ParsePush push = new ParsePush();
                    Log.d("\"user_\" + need.getUser().getId().toString()", "user_" + need.getUser().getId().toString());
                    push.setChannel("user_" + need.getUser().getId().toString());
                    push.setMessage(currentUser.getFirstname() + " vient de répondre à votre annonce dans " + group.getName());
                    push.sendInBackground();

                    String annonce = need.getUser().getFirstname() + " a été prévenu.";
                    Toast.makeText(ListNeedsActivity.this, annonce, Toast.LENGTH_LONG).show();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        ImageButton removeButton = (ImageButton) view.findViewById(R.id.remove_button);
                        removeButton.setSelected(false);
                        needs.get(group).remove(need);
                        ((GroupNeedListAdapter) groupsAdapter).notifyDataSetChanged();
                    }
                }, 500);
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

