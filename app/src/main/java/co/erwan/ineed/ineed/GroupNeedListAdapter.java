package co.erwan.ineed.ineed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.erwan.ineed.ineed.Helpers.DownloadImageTask;

/**
 * Created by erwanmartin on 16/12/2014.
 */
public class GroupNeedListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Group> listDataHeader;
    private HashMap<Group, List<Need>> listDataChild;

    public GroupNeedListAdapter(Context context, List<Group> listDataHeader, HashMap<Group, List<Need>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Group getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public Need getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String groupNameValue = (String) getGroup(groupPosition).getName();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.view_header_need_list_needs, null);
        }

        View groupContainer = convertView.findViewById(R.id.group_container);
        groupContainer.setBackgroundColor(groupPosition % 2 == 0 ? context.getResources().getColor(R.color.pair_item) : context.getResources().getColor(R.color.impair_item));

        TextView groupName = (TextView) convertView.findViewById(R.id.group_name);
        groupName.setText(groupNameValue);

        Integer countNeedsValue = getChildrenCount(groupPosition);

        Integer stringId = countNeedsValue > 1 ? R.string.count_needs_string_plurar : R.string.count_needs_string_singular;

        TextView countNeeds = (TextView) convertView.findViewById(R.id.count_needs);
        countNeeds.setText(countNeedsValue.toString() + " " + context.getResources().getString(stringId));

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String needContentValue = (String) getChild(groupPosition, childPosition).getContent();
        final User user = (User) getChild(groupPosition, childPosition).getUser();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.view_need_list_needs, null);
        }

        TextView needContent = (TextView) convertView.findViewById(R.id.need_content);
        needContent.setText(needContentValue);

        TextView user_firstname = (TextView) convertView.findViewById(R.id.user_firstname);
        user_firstname.setText(user.getFirstname());

        Button removeButton = (Button) convertView.findViewById(R.id.remove_button);
        removeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(context instanceof ListNeedsActivity){
                    ((ListNeedsActivity)context).removeNeed(getGroup(groupPosition), getChild(groupPosition, childPosition));
                }
            }

        });

        new DownloadImageTask((ImageView) convertView.findViewById(R.id.user_picture))
                .execute(user.getPicture());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

