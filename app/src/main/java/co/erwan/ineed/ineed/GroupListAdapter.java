package co.erwan.ineed.ineed;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by erwanmartin on 15/12/2014.
 */

public class GroupListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Group> groups;

    @Override
    public int getCount() { return groups.size(); }

    @Override
    public Group getItem(int position) {
        return groups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int viewLayout = R.layout.view_group_select_groups;

        View rowView = inflater.inflate(viewLayout, parent, false);

        TextView groupName = (TextView) rowView.findViewById(R.id.group_name);
        groupName.setText(getItem(position).getName());

        TextView countMembers = (TextView) rowView.findViewById(R.id.count_members);
        Integer countMembersValue = getItem(position).getCountMembers();

        Integer stringId = countMembersValue > 1 ? R.string.count_members_string_plurar : R.string.count_members_string_singular;
        countMembers.setText(countMembersValue.toString() + " " + context.getResources().getString(stringId));

        View groupContainer = rowView.findViewById(R.id.group_container);
        groupContainer.setBackgroundColor(getItem(position).getSelected() ? context.getResources().getColor(R.color.green_selection) : context.getResources().getColor(R.color.grey_item));

        return rowView;
    }

    public GroupListAdapter(Context context, ArrayList<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

    public void updateReceiptsList(ArrayList<Group> newGroups) {
        groups.clear();
        groups.addAll(newGroups);
        this.notifyDataSetChanged();
    }
}
