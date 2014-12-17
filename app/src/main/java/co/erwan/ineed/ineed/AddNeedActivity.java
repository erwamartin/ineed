package co.erwan.ineed.ineed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userActions = new UserActions(this);

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

                Intent listNeedsActivity = new Intent(AddNeedActivity.this, ListNeedsActivity.class);
                startActivity(listNeedsActivity);
            }

        });
    }
}
