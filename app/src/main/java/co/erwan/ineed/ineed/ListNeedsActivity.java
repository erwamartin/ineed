package co.erwan.ineed.ineed;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
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

    private List<Group> groups;
    private HashMap<Group, List<Need>> needs;
    private GroupNeedListAdapter groupsAdapter;
    private ExpandableListView groupsList;
    private UserActions userActions;
    private User currentUser;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userActions = new UserActions(this);

        groups = new ArrayList<Group>();

        setContentView(R.layout.activity_list_needs);
        groupsList = (ExpandableListView) findViewById(R.id.groupsList);

        currentUser = userActions.getCurrentUser();
        groups = currentUser.getSelectedGroups();

        getNeeds();

        groupsAdapter = new GroupNeedListAdapter(ListNeedsActivity.this, groups, needs);
        groupsList.setAdapter(groupsAdapter);

        TextView user_firstname = (TextView) findViewById(R.id.user_firstname);
        user_firstname.setText(currentUser.getFirstname());

        new DownloadImageTask((ImageView) findViewById(R.id.user_picture))
                .execute(currentUser.getPicture());

    }

    private void getNeeds() {
        needs = new HashMap<Group, List<Need>>();

        for (Group g : groups) {
            List<Need> needList = new ArrayList<Need>();
            Need newNeed = new Need("J'ai besoin de sucre", currentUser);
            needList.add(newNeed);
            needs.put(g, needList);
        }
    }
}

