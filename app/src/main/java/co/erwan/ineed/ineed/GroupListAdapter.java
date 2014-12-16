package co.erwan.ineed.ineed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

/**
 * Created by erwanmartin on 15/12/2014.
 */

public class GroupListAdapter extends ArrayAdapter<Group> {

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
        countMembers.setText(groups[position].getCountMembers().toString());

        String coverUrl = groups[position].getCoverUrl();
        Log.d("coverUrl", coverUrl);
        if (coverUrl != null && !coverUrl.equals("")) {
            new DownloadImageTask((ImageView) rowView.findViewById(R.id.group_cover))
                    .execute(coverUrl);
        }

        return rowView;
    }

    public GroupListAdapter(Context context, Group[] groups) {
        super(context, R.layout.activity_select_groups, groups);
        this.groups = groups;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
