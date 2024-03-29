package co.erwan.ineed.ineed;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.erwan.ineed.ineed.Adapters.GroupListAdapter;
import co.erwan.ineed.ineed.Helpers.DownloadImageTask;
import co.erwan.ineed.ineed.Models.Group;

/**
 * Created by erwanmartin on 17/12/2014.
 */
public class AddNeedActivity extends Activity {

    private ArrayList<Group> groups;
    private ArrayList<Group> selectedGroups;
    private GroupListAdapter groupsAdapter;
    private ListView groupsList;

    ProgressDialog progressDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_need);
        groupsList = (ListView) findViewById(R.id.groups_list);

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

                ((GroupListAdapter) groupsAdapter).notifyDataSetChanged();

            }
        });

        Button nextStepButton = (Button)findViewById(R.id.add_need_button);
        nextStepButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                EditText needContent = (EditText)findViewById(R.id.need_content);
                String needContentValue = needContent.getText().toString();

                if (needContentValue.length() > 5) {

                    if (selectedGroups.size() > 0) {

                        JSONObject jsonParams = new JSONObject();

                        try {

                            jsonParams.put("user", currentUser.getId().toString());
                            jsonParams.put("message", needContentValue);

                            JSONArray jsonGroups = new JSONArray();

                            for (Group g : selectedGroups) {
                                jsonGroups.put(g.getId().toString());
                            }

                            jsonParams.put("group", jsonGroups);

                            addNeedAPI(jsonParams);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(AddNeedActivity.this, "Vous devez choisir au moins un groupe", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(AddNeedActivity.this, "Veuillez écrire une annonce", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    private void addNeedAPI (JSONObject params) {
        String url = this.getResources().getString(R.string.server_path) + this.getResources().getString(R.string.add_need);

        progressDialog = ProgressDialog.show(context, "Envoi en cours",
                "Veuillez patienter", true);

        JsonObjectRequest createUserRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, params, new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // TODO Auto-generated method stub

                for (Group g : selectedGroups) {

                    String message= currentUser.getFirstname() + " vient de poster une annonce dans " + g.getName();

                    ParsePush push = new ParsePush();
                    ParseQuery pushQuery = ParseInstallation.getQuery();
                    pushQuery.whereEqualTo("channels", "group_" + g.getId());
                    pushQuery.whereNotEqualTo("channels", "user_" + currentUser.getId().toString());
                    push.setMessage(message);
                    push.setQuery(pushQuery);
                    push.sendInBackground();
                }

                // Hide loader
                progressDialog.dismiss();

                Toast.makeText(AddNeedActivity.this, "Votre annonce a été publiée", Toast.LENGTH_LONG).show();
                Intent listNeedsActivity = new Intent(AddNeedActivity.this, ListNeedsActivity.class);
                startActivity(listNeedsActivity);
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

                // Hide loader
                progressDialog.dismiss();
            }
        });

        mVolleyRequestQueue.add(createUserRequest);
    }

    @Override
    public void onBackPressed() {
        Intent listNeedsActivity = new Intent(getApplicationContext(), ListNeedsActivity.class);
        startActivity(listNeedsActivity);
        finish();
    }
}
