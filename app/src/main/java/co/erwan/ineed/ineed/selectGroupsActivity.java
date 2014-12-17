package co.erwan.ineed.ineed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    private List<Group> groups;
    private GroupListAdapter groupsAdapter;
    private ListView groupsList;
    private UserActions userActions;
    private User currentUser;
    private ArrayList<Group> selectedGroups;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groups = new ArrayList<Group>();
        selectedGroups = new ArrayList<Group>();

        setContentView(R.layout.activity_select_groups);

        userActions = new UserActions(this);
        groupsList = (ListView) findViewById(R.id.groupsList);

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

            View groupView = (View) groupsList.getChildAt(position - start);
            View groupContainer = groupView.findViewById(R.id.group_container);
            groupContainer.setBackgroundColor(!isSelected ? Color.argb(65, 60, 138, 36) : getResources().getColor(R.color.grey_background));

            Log.d("CLICK", groups.get(position).getName());


            }
        });

        Button nextStepButton = (Button)findViewById(R.id.next_step_button);
        nextStepButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            currentUser.setSelectedGroups(selectedGroups);
            userActions.setCurrentUser(currentUser);
            Intent listNeedsActivity = new Intent(SelectGroupsActivity.this, ListNeedsActivity.class);
            startActivity(listNeedsActivity);
            }

        });

        this.getUserData();

        this.getUserGroups();
    }

    public void refreshUserData(){
        User current_user = userActions.getCurrentUser();

        Log.d("current_user", current_user.toString());

        TextView user_firstname = (TextView) findViewById(R.id.user_firstname);
        user_firstname.setText(current_user.getFirstname());

        new DownloadImageTask((ImageView) findViewById(R.id.user_picture))
                .execute(current_user.getPicture());
    }

    protected void getUserData() {
        Session session = Session.getActiveSession();

        Bundle params = new Bundle();
        params.putString("fields", "id,first_name,last_name,name,picture");

        final SelectGroupsActivity _this = this;

        new Request(
                session,
                "/me",
                params,
                HttpMethod.GET,
                new Request.Callback() {
                    public JSONObject onCompleted(Response response) {
                        GraphObject graphObject = response.getGraphObject();

                        Log.d("graphObjectUSER", graphObject.getProperty("id").toString());

                        FacebookRequestError error = response.getError();

                        if (error == null) {

                            User newUser = new User(Long.parseLong(graphObject.getProperty("id").toString()), graphObject.getProperty("name").toString());
                            newUser.setFirstname(graphObject.getProperty("first_name").toString());

                            JSONObject json_picture = (JSONObject) graphObject.getProperty("picture");
                            try {
                                JSONObject picture_data = json_picture.getJSONObject("data");
                                newUser.setPicture(picture_data.getString("url"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                currentUser = newUser;
                                userActions.setCurrentUser(newUser);
                                _this.refreshUserData();
                            }

                        }

                        return null;
                    }
                }
        ).executeAsync();
    }

    protected void addItemListGroups(Group group) {

        groups.add(group);

        if (groups.size() == 1) {
            groupsAdapter = new GroupListAdapter(SelectGroupsActivity.this, groups.toArray(new Group[groups.size()]));
            groupsList.setAdapter(groupsAdapter);
        } else {
            //groupsAdapter.add(group);
            groupsAdapter = new GroupListAdapter(SelectGroupsActivity.this, groups.toArray(new Group[groups.size()]));
            groupsList.setAdapter(groupsAdapter);
            //groupsAdapter.notifyDataSetChanged();
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
