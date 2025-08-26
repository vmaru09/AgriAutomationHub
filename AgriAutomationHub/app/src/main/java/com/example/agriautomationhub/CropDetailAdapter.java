package com.example.agriautomationhub;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.Spannable;

import java.util.HashMap;
import java.util.List;

public class CropDetailAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "CropDetailAdapter";
    private final Context context;
    private final List<String> listDataHeader;
    private final HashMap<String, List<Object>> listDataChild;

    public CropDetailAdapter(Context context, List<String> listDataHeader, HashMap<String, List<Object>> listDataChild) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;

    }

    @Override
    public int getGroupCount() {
        int count = (listDataHeader == null) ? 0 : listDataHeader.size();
        return count;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (listDataHeader == null) {
            Log.w(TAG, "getChildrenCount: headers null, returning 0");
            return 0;
        }
        if (groupPosition < 0 || groupPosition >= listDataHeader.size()) {
            Log.w(TAG, "getChildrenCount: invalid groupPosition=" + groupPosition);
            return 0;
        }
        String header = listDataHeader.get(groupPosition);
        List<Object> children = (listDataChild == null) ? null : listDataChild.get(header);
        int size = (children == null) ? 0 : children.size();
        return size;
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (listDataHeader == null || groupPosition < 0 || groupPosition >= listDataHeader.size()) {
            Log.w(TAG, "getGroup: out of bounds groupPosition=" + groupPosition);
            return "";
        }
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (listDataHeader == null || listDataChild == null) {
            Log.w(TAG, "getChild: headers or child map null");
            return "";
        }
        if (groupPosition < 0 || groupPosition >= listDataHeader.size()) {
            Log.w(TAG, "getChild: invalid groupPosition=" + groupPosition);
            return "";
        }
        String header = listDataHeader.get(groupPosition);
        List<Object> children = listDataChild.get(header);
        if (children == null || childPosition < 0 || childPosition >= children.size()) {
            Log.w(TAG, "getChild: invalid childPosition=" + childPosition + " for header=" + header);
            return "";
        }
        Object value = children.get(childPosition);
        return (value == null) ? "" : value;
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

    // Group (header) view
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.list_group, parent, false);
            }

            TextView lblListHeader = convertView.findViewById(R.id.group_header);
            Object g = getGroup(groupPosition);
            String header = (g == null) ? "" : g.toString();
            lblListHeader.setText(header);

            // DEBUG: force visible text color if you suspect theming issues (uncomment to test)
            // lblListHeader.setTextColor(Color.BLACK);

            return convertView;
        } catch (Exception e) {
            Log.e(TAG, "getGroupView: exception", e);
            return convertView;
        }
    }

    // Child view
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.list_item, parent, false);
            }

            TextView txtListChild = convertView.findViewById(R.id.item_text);
            Object childObj = getChild(groupPosition, childPosition);
            String text = (childObj == null) ? "" : childObj.toString();

            // Log basic info
            int len = (text == null) ? 0 : text.length();
            String preview = (len > 120) ? text.substring(0, 120) + "..." : text;

            // Apply bold until colon (same behavior as before)
            SpannableString spannableString = new SpannableString(text);
            int colonIndex = text.indexOf(":");
            if (colonIndex > 0) {
                spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, colonIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            txtListChild.setText(spannableString);

            // DEBUG: force visible text color if you suspect theming issues (uncomment to test)
            // txtListChild.setTextColor(Color.BLACK);

            // Ensure visible
            txtListChild.setVisibility(View.VISIBLE);

            return convertView;
        } catch (Exception e) {
            Log.e(TAG, "getChildView: exception", e);
            return convertView;
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
