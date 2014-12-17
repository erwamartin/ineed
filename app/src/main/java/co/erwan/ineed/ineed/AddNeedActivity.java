package co.erwan.ineed.ineed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.erwan.ineed.ineed.Helpers.DownloadImageTask;

/**
 * Created by erwanmartin on 17/12/2014.
 */
public class AddNeedActivity extends Activity {

    private UserActions userActions;
    private User currentUser;
    private ArrayList<Group> groups;
    private ArrayList<Group> selectedGroups;
    private GroupListAdapter groupsAdapter;
    private ListView groupsList;

    private RequestQueue mVolleyRequestQueue;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userActions = new UserActions(this);

        mVolleyRequestQueue = Volley.newRequestQueue(this);
        mVolleyRequestQueue.start();

        setContentView(R.layout.activity_add_need);
        groupsList = (ListView) findViewById(R.id.groups_list);

        currentUser = userActions.getCurrentUser();

        selectedGroups = new ArrayList<Group>();
        groups = (ArrayList<Group>)currentUser.getSelectedGroups().clone();

        for (Group g : groups) {
            g.setSelected(false);
        }

        TextView user_firstname = (TextView) findViewById(R.id.user_firstname);
        user_firstname.setText(currentUser.getFirstname());

        new DownloadImageTask((ImageView) findViewById(R.id.user_picture))
                .execute(currentUser.getPicture());

        groupsAdapter = new GroupListAdapter(AddNeedActivity.this, groups);
        groupsList.setAdapter(groupsAdapter);

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

            }
        });

        Button nextStepButton = (Button)findViewById(R.id.add_need_button);
        nextStepButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                EditText needContent = (EditText)findViewById(R.id.need_content);
                String needContentValue = needContent.getText().toString();

                JSONObject jsonParams = new JSONObject();

                try {

                    jsonParams.put("user",currentUser.getId().toString());
                    jsonParams.put("message",needContentValue);

                    JSONArray jsonGroups = new JSONArray();

                    for (Group g : selectedGroups) {
                        jsonGroups.put(g.getId().toString());
                    }

                    jsonParams.put("group", jsonGroups);

                    addNeedAPI(jsonParams);
                    Log.d("jsonParams", jsonParams.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent listNeedsActivity = new Intent(AddNeedActivity.this, ListNeedsActivity.class);
                startActivity(listNeedsActivity);
            }

        });
    }

    private void addNeedAPI (JSONObject params) {
        String url = this.getResources().getString(R.string.server_path) + this.getResources().getString(R.string.add_need);

        JsonObjectRequest createUserRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, params, new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // TODO Auto-generated method stub
                Log.d("addNeedAPI", response.toString());
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d("addNeedAPI", error.toString());
            }
        });

        mVolleyRequestQueue.add(createUserRequest);
    }
}
