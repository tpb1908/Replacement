package com.tpb.timetable.Home.Input;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.Subject;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.ColorResources;

/**
 * Created by theo on 20/04/16.
 */
public class SubjectSpinnerAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {
    private static final String TAG = "SubjectSpinnerAdapter";
    private Context mContext;
    private DBHelper.ArrayWrapper<Subject> mSubjects;


    public SubjectSpinnerAdapter(Context context, DBHelper.ArrayWrapper<Subject> subjects) {
        super();
        this.mContext = context;
        this.mSubjects = subjects;
    }

    public int getPositionOfSubject(int subjectID) {
        int pos = -1;
        for(int i = 0; i < mSubjects.size(); i++) {
            if(mSubjects.get(i).getID() == subjectID) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    @Override
    public int getCount () {
        return mSubjects.size();
    }

    @Override
    public Object getItem (int position) {
        return mSubjects.get(position);
    }

    @Override
    public long getItemId (int position) {
        return mSubjects.get(position).getID();
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @Override
    public View getDropDownView (int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    private View getCustomView(int position, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = inflater.inflate(R.layout.listitem_subject_spinner, parent, false); //False is important. It indicates whether the view should be added directly to the ViewGroup
        final TextView name = (TextView) row.findViewById(R.id.text_subject);
        final View colourBar = row.findViewById(R.id.colour_bar);
        final String TEXT = mSubjects.get(position).getName() + " " + mSubjects.get(position).getTeacher();
        name.setText(TEXT);
        colourBar.setBackgroundColor(mSubjects.get(position).getColor());
        if(ColorResources.darkTheme) {
            row.setBackgroundColor(mContext.getResources().getColor(R.color.grey_800));
            name.setTextColor(mContext.getResources().getColor(R.color.colorLightText));
        }

        return row;
    }
}

