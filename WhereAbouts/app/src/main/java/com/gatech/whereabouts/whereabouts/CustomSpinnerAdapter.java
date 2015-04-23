package com.gatech.whereabouts.whereabouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ksion on 4/23/15.
 */
public class CustomSpinnerAdapter<T> extends ArrayAdapter<T> {
    private Context context;
    private int layout;
    private List<T> items;

    public CustomSpinnerAdapter(Context context, int layout, ArrayList<T> items) {
        super(context, layout, items);
        this.context = context;
        this.layout = layout;
        this.items = items;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView);
    }

    public View getCustomView(int position, View convertView) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(layout, null);
        }

        TextView textViewItem = (TextView) convertView.findViewById(R.id.item);
        textViewItem.setText(items.get(position).toString());

        return convertView;
    }
}
