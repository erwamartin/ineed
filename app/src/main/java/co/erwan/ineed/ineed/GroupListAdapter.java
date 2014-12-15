package co.erwan.ineed.ineed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by erwanmartin on 15/12/2014.
 */

public class GroupListAdapter extends ArrayAdapter<Group> {

    private Group[] groups;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int viewLayout = R.layout.activity_single_group;

        View rowView = inflater.inflate(viewLayout, parent, false);

        TextView groupName = (TextView) rowView.findViewById(R.id.group_name);
        groupName.setText(groups[position].getName());

        TextView countMembers = (TextView) rowView.findViewById(R.id.count_members);
        countMembers.setText(groups[position].getCountMembers().toString());

        return rowView;
    }

    public GroupListAdapter(Context context, Group[] groups) {
        super(context, R.layout.activity_select_groups, groups);
        this.groups = groups;
    }
}
