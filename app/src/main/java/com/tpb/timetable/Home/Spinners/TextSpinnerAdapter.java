package com.tpb.timetable.Home.Spinners;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tpb.timetable.Utils.UIHelper;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by theo on 17/10/16.
 */

public class TextSpinnerAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {
    private static final String TAG = "TextSpinnerAdapter";
    private ArrayList<String> options;

    public TextSpinnerAdapter(ArrayList<String> options) {
        super();
        this.options = options;
    }

    public TextSpinnerAdapter(String[] options) {
        super();
        this.options = new ArrayList<>(Arrays.asList(options));
    }

    public void setData(String[] options) {
        this.options = new ArrayList<>(Arrays.asList(options));
        notifyDataSetChanged();
    }

    public int getPosition(String s) {
        return options.indexOf(s);
    }

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public Object getItem(int i) {
        return options.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return getCustomView(i, viewGroup);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    private View getCustomView(int pos, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //False is important. It indicates whether the view should be added directly to the ViewGroup
        final View row = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        final TextView tv = (TextView) row.findViewById(android.R.id.text1);
        row.setBackgroundColor(UIHelper.getCardBackground());
        tv.setText(options.get(pos));
        tv.setTextColor(UIHelper.getPrimaryText());
        return row;
    }
}
