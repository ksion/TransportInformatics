package com.gatech.whereabouts.whereabouts;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Moon Chang on 4/9/2015.
 */
public class ArrayAdapterUserDataStruct extends ArrayAdapter<UserDataStruct> {
    private Context mContext;
    private int layoutResourceId;
    private List<UserDataStruct> data;

    public ArrayAdapterUserDataStruct(Context context, int resource, List<UserDataStruct> objects) {
        super(context, resource, objects);
        mContext = context;
        layoutResourceId = resource;
        data = objects;
    }

    @Override
    public View getView(int position, View cView, ViewGroup parent) {
        View convertView = cView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        UserDataStruct item = getItem(position);

        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        TextView tv_purpose = (TextView) convertView.findViewById(R.id.tv_purpose);
        TextView tv_tag = (TextView) convertView.findViewById(R.id.tv_tag);
        TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);

        tv_name.setText(item.placeName);
        tv_purpose.setText(item.tripPurpose);
        tv_tag.setText(item.tags);

        Log.d("ArrayAdapter", item.tags);
        //tv_time.setText(item.startDateTime.toString());

        return convertView;
    }

    public int getCount() {
        return data.size();
    }
}
