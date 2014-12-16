package co.erwan.ineed.ineed;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by erwanmartin on 15/12/2014.
 */
public class SelectGroupsActivity extends Activity {

    private static final String TAG = MainFragment.class.getSimpleName();

    private List<Group> groups;
    private GroupListAdapter groupsAdapter;
    private ListView groupsList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groups = new ArrayList<Group>();

        setContentView(R.layout.activity_select_groups);

        this.getUserGroups();
    }

    protected void addItemListGroups(Group group) {

        groups.add(group);

        if (groups.size() == 1) {
            groupsList = (ListView) findViewById(R.id.groupsList);
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

                                    if (groupData.has("cover")) {
                                        JSONObject cover_data = groupData.getJSONObject("cover");
                                        Log.d("cover_data", cover_data.toString());
                                        group.setCoverUrl(cover_data.getString("source"));
                                    }
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

                                    usersOfGroup.add(new User(graphMember.getInt("id"), graphMember.getString("name")));
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
