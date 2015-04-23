package com.gatech.whereabouts.whereabouts;

/**
 * Created by liumomo610 on 4/16/15.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class ExpandableSelectListAdapter<T> extends BaseExpandableListAdapter {

    private Context context;
    private List<T> listGroup;
    private HashMap<T, List<T>> listChild;
    private int parentLayout, childLayout;

    public ExpandableSelectListAdapter(Context context, List<T> listGroup,
                                    HashMap<T, List<T>> listChild,
                                    int parentLayout, int childLayout) {
        this.context = context;
        this.listGroup = listGroup;
        this.listChild = listChild;
        this.parentLayout = parentLayout;
        this.childLayout = childLayout;
    }

    @Override
    public int getGroupCount() {
        return listGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listChild.get(listGroup.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listChild.get(listGroup.get(groupPosition)).get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(parentLayout, null);
        }

        T textGroup = (T) getGroup(groupPosition);

        TextView textViewGroup = (TextView) convertView
                .findViewById(R.id.group);
        textViewGroup.setText(String.valueOf(textGroup.toString()));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(childLayout, null);
        }

        TextView textViewItem = (TextView) convertView.findViewById(R.id.item);

        T text = (T) getChild(groupPosition, childPosition);

        textViewItem.setText(text.toString());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}