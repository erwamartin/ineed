package co.erwan.ineed.ineed;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by erwanmartin on 16/12/2014.
 */
public class GroupNeedListAdapter extends ArrayAdapter<Group> {

    private Group[] groups;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int viewLayout = R.layout.item_list_groups;

        View rowView = inflater.inflate(viewLayout, parent, false);

        TextView groupName = (TextView) rowView.findViewById(R.id.group_name);
        groupName.setText(groups[position].getName());

        TextView countMembers = (TextView) rowView.findViewById(R.id.count_members);
        Integer countMembersValue = groups[position].getCountMembers();

        Integer stringId = countMembersValue > 1 ? R.string.count_members_string_plurar : R.string.count_members_string_singular;
        countMembers.setText(countMembersValue.toString() + " " + getContext().getResources().getString(stringId));

        return rowView;
    }

    public GroupNeedListAdapter(Context context, Group[] groups) {
        super(context, R.layout.activity_select_groups, groups);
        this.groups = groups;
    }
}

