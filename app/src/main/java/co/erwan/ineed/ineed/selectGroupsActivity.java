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

        new Request(
                session,
                "/me/groups",
                null,
                HttpMethod.GET,
                new Request.Callback() {
                    public JSONObject onCompleted(Response response) {
                        GraphObject graphObject = response.getGraphObject();

                        //GraphObjectList<GraphObject> data = graphObject.getPropertyAsList("data", GraphObject.class);
                        //Log.d("Groups", data.toString());

                        FacebookRequestError error = response.getError();

                        if (graphObject.getProperty("data") != null) {
                            try {
                                // Get the data, parse info to get the key/value info
                                JSONArray jArray = (JSONArray)graphObject.getProperty("data");

                                for(int i=0;i<jArray.length();i++){
                                    JSONObject json_data = jArray.getJSONObject(i);

                                    String groupId = json_data.getString("id");

                                    addGroupData(groupId);

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

    protected void addGroupData(String groupId) {
        Session session = Session.getActiveSession();

        new Request(
                session,
                "/" + groupId,
                null,
                HttpMethod.GET,
                new Request.Callback() {
                    public JSONObject onCompleted(Response response) {
                        GraphObject groupData = response.getGraphObject();

                        Log.d("graphObject", groupData.toString());

                        FacebookRequestError error = response.getError();

                        if (error == null) {

                            addItemListGroups(new Group(groupData.getProperty("id").toString(), groupData.getProperty("name").toString()));

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
