package co.erwan.ineed.ineed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.erwan.ineed.ineed.Helpers.DownloadImageTask;

/**
 * Created by erwanmartin on 15/12/2014.
 */
public class SelectGroupsActivity extends Activity {

    private static final String TAG = MainFragment.class.getSimpleName();

    private ArrayList<Group> groups;
    private GroupListAdapter groupsAdapter;
    private ListView groupsList;
    private UserActions userActions;
    private User currentUser;
    private ArrayList<Group> selectedGroups;

    private RequestQueue mVolleyRequestQueue;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groups = new ArrayList<Group>();
        selectedGroups = new ArrayList<Group>();

        mVolleyRequestQueue = Volley.newRequestQueue(this);
        mVolleyRequestQueue.start();

        setContentView(R.layout.activity_select_groups);

        userActions = new UserActions(this);
        groupsList = (ListView) findViewById(R.id.groups_list);

        groupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
            Group currentGroup = groups.get(position);
            Boolean isSelected = currentGroup.getSelected();
            currentGroup.setSelected(! isSelected);

            if (! isSelected)   selectedGroups.add(currentGroup);
            else                selectedGroups.remove(currentGroup);

            int start = groupsList.getFirstVisiblePosition();

            //View groupView = (View) groupsList.getChildAt(position - start);
            //View groupContainer = groupView.findViewById(R.id.group_container);
            //groupContainer.setBackgroundColor(!isSelected ? Color.argb(65, 60, 138, 36) : getResources().getColor(R.color.grey_background));

            ((GroupListAdapter) groupsAdapter).notifyDataSetChanged();
            //Log.d("CLICK", groups.get(position).getName());


            }
        });

        Button nextStepButton = (Button)findViewById(R.id.next_step_button);
        nextStepButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            createUser();

            currentUser.setSelectedGroups(selectedGroups);
            userActions.setCurrentUser(currentUser);
            Intent listNeedsActivity = new Intent(SelectGroupsActivity.this, ListNeedsActivity.class);
            startActivity(listNeedsActivity);
            }

        });

        //this.getUserData();
        refreshUserData();

        this.getUserGroups();
    }

    public void refreshUserData(){
        currentUser = userActions.getCurrentUser();

        Log.d("current_user", currentUser.toString());

        TextView user_firstname = (TextView) findViewById(R.id.user_firstname);
        user_firstname.setText(currentUser.getFirstname());

        new DownloadImageTask((ImageView) findViewById(R.id.user_picture))
                .execute(currentUser.getPicture());
    }

    private void createUser() {
        JSONObject jsonParams = new JSONObject();

        try {
            JSONArray jsonUsers = new JSONArray();
            JSONObject jsonUser = new JSONObject();

            jsonUser.put("id",currentUser.getId().toString());
            jsonUser.put("firstname",currentUser.getFirstname());
            jsonUser.put("picture",currentUser.getPicture());

            jsonUsers.put(jsonUser);
            jsonParams.put("user", jsonUsers);

            JSONArray jsonGroups = new JSONArray();

            for (Group g : selectedGroups) {
                JSONObject jsonGroup = new JSONObject();

                jsonGroup.put("id",g.getId());
                jsonGroup.put("name",g.getName());

                jsonGroups.put(jsonGroup);
            }

            jsonParams.put("group", jsonGroups);

            createUserAPI(jsonParams);
            Log.d("jsonParams", jsonParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createUserAPI(JSONObject params) {
        String url = this.getResources().getString(R.string.server_path) + this.getResources().getString(R.string.create_user);

        JsonObjectRequest createUserRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, params, new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // TODO Auto-generated method stub
                Log.d("createUser", response.toString());
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

    protected void addItemListGroups(Group group) {

        groups.add(group);

        if (groups.size() == 1) {
            groupsAdapter = new GroupListAdapter(SelectGroupsActivity.this, groups);
            groupsList.setAdapter(groupsAdapter);
        } else {
            //groupsAdapter.add(group);
            //groupsAdapter = new GroupListAdapter(SelectGroupsActivity.this, groups.toArray(new Group[groups.size()]));
            //groupsList.setAdapter(groupsAdapter);
            //groupsAdapter.notifyDataSetChanged();

            ((GroupListAdapter) groupsAdapter).notifyDataSetChanged();
        }
    }

    protected void getUserGroups() {
        Session session = Session.getActiveSession();

        final SelectGroupsActivity _this = this;

        Bundle params = new Bundle();
        params.putString("fields", "groups{id,name,cover}");

        new Request(
                session,
                "/me",
                params,
                HttpMethod.GET,
                new Request.Callback() {
                    public JSONObject onCompleted(Response response) {
                        GraphObject graphObject = response.getGraphObject();

                        Log.d("graphObjectGROUPS", response.toString());

                        //GraphObjectList<GraphObject> data = graphObject.getPropertyAsList("data", GraphObject.class);
                        //Log.d("Groups", data.toString());

                        FacebookRequestError error = response.getError();

                        if (graphObject.getProperty("groups") != null) {
                            try {
                               // Get the data, parse info to get the key/value info
                                JSONObject groups_data = (JSONObject)graphObject.getProperty("groups");
                                JSONArray jArray = groups_data.getJSONArray("data");

                                for(int i=0;i<jArray.length();i++){
                                    JSONObject groupData = jArray.getJSONObject(i);

                                    //addGroupData(groupId);

                                    Group group = new Group(groupData.getString("id"), groupData.getString("name").toString());
                                    addGroupMembers(group);

                                    //addItemListGroups(new Group(json_data.getInt("id"), json_data.getString("name")));
                               }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                String message = "Error getting request info";
                                Log.e(TAG, message);

                            }
                        } else if (error != null) {
                            String message = "Error getting request info";
                            Log.e(TAG, message);
                        }
                        return null;
                    }
                }
        ).executeAsync();
    }

    protected void addGroupMembers(final Group group) {
        Session session = Session.getActiveSession();

        new Request(
                session,
                "/" + group.getId() + "/members",
                null,
                HttpMethod.GET,
                new Request.Callback() {
                    public JSONObject onCompleted(Response response) {
                        GraphObject graphObject = response.getGraphObject();

                        FacebookRequestError error = response.getError();

                        if (error == null) {

                            JSONArray jArray = (JSONArray)graphObject.getProperty("data");

                            ArrayList<User> usersOfGroup = new ArrayList<User>();

                            for(int i=0;i<jArray.length();i++) {
                                try {
                                    JSONObject graphMember = jArray.getJSONObject(i);

                                    usersOfGroup.add(new User(graphMember.getLong("id"), graphMember.getString("name")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            group.setMembers(usersOfGroup);

                            addItemListGroups(group);

                        } else {
                            String message = "Error getting request info";
                            Log.e(TAG, message);
                        }
                        return null;
                    }
                }
        ).executeAsync();
    }

}
