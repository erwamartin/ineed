package co.erwan.ineed.ineed;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.parse.ParsePush;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.erwan.ineed.ineed.Adapters.GroupListAdapter;
import co.erwan.ineed.ineed.Helpers.DownloadImageTask;
import co.erwan.ineed.ineed.Models.Group;

/**
 * Created by erwanmartin on 15/12/2014.
 */
public class SelectGroupsActivity extends Activity {

    private ArrayList<Group> groups;
    private GroupListAdapter groupsAdapter;
    private ListView groupsList;
    private ArrayList<Group> selectedGroups;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groups = new ArrayList<Group>();
        selectedGroups = new ArrayList<Group>();

        setContentView(R.layout.activity_select_groups);

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

            ((GroupListAdapter) groupsAdapter).notifyDataSetChanged();

            }
        });

        Button nextStepButton = (Button)findViewById(R.id.next_step_button);
        nextStepButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            createUser();

            currentUser.setSelectedGroups(selectedGroups);
            userActions.setCurrentUser(currentUser);
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

                ParsePush.subscribeInBackground("user_" + currentUser.getId());

                for (Group g : selectedGroups) {
                    ParsePush.subscribeInBackground("group_" + g.getId());
                }

                Intent listNeedsActivity = new Intent(SelectGroupsActivity.this, ListNeedsActivity.class);
                startActivity(listNeedsActivity);
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d("createUser", error.toString());
                Toast.makeText(SelectGroupsActivity.this, "Une erreur s'est produite.", Toast.LENGTH_LONG).show();
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

                            JSONArray groupUsers = (JSONArray)graphObject.getProperty("data");

                            group.setCountMembers(groupUsers.length());

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
