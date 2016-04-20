package com.anapp.tpb.replacement.Home.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;

import java.util.ArrayList;

/**
 * Created by theo on 20/04/16.
 */
public class SubjectSpinnerAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {
    private Context context;
    private ArrayList<Subject> subjects;


    @Override
    public int getCount () {
        return subjects.size();
    }

    @Override
    public Object getItem (int position) {
        return null;
    }

    @Override
    public long getItemId (int position) {
        return 0;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    public SubjectSpinnerAdapter(Context context, ArrayList<Subject> subjects) {
        super();
        this.context = context;
        this.subjects = subjects;
    }

    @Override
    public View getDropDownView (int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    public View getCustomView(int position, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View row = inflater.inflate(R.layout.subject_spinner_layout, parent, false); //False is important. It indicates whether the view should be added directly to the ViewGroup
        TextView name = (TextView) row.findViewById(R.id.subjectText);
        View colourBar = row.findViewById(R.id.colourBar);
        String text = this.subjects.get(position).getName() + ", " + this.subjects.get(position).getTeacher();
        name.setText(text);
        colourBar.setBackgroundColor(this.subjects.get(position).getColor());
        return row;
    }
}

