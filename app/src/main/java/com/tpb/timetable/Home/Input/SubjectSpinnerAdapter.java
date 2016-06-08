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
import com.tpb.timetable.Utils.ThemeHelper;

/**
 * Created by theo on 20/04/16.
 */
public class SubjectSpinnerAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {
    private static final String TAG = "SubjectSpinnerAdapter";
    private final Context mContext;
    private final DBHelper.ArrayWrapper<Subject> mSubjects;

    public SubjectSpinnerAdapter(Context context, DBHelper.ArrayWrapper<Subject> subjects) {
        super();
        this.mContext = context;
        this.mSubjects = subjects;
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
        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //False is important. It indicates whether the view should be added directly to the ViewGroup
        final View row = inflater.inflate(R.layout.listitem_subject_spinner, parent, false);
        final TextView name = (TextView) row.findViewById(R.id.text_subject);
        final View colourBar = row.findViewById(R.id.colour_bar);
        final String subjectDescription = mSubjects.get(position).getName() +
                " " + mSubjects.get(position).getTeacher();
        name.setText(subjectDescription);
        colourBar.setBackgroundColor(mSubjects.get(position).getColor());
        //No point sending a single TextView and colored view to be themed
        row.setBackgroundColor(ThemeHelper.getCardBackground());
        name.setTextColor(ThemeHelper.getPrimaryText());
        return row;
    }

    public int getPositionOfSubject(int subjectID) {
        return mSubjects.getPosOfID(subjectID);
    }
}

