package com.anapp.tpb.replacement.Home.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;

/**
 * Created by theo on 20/04/16.
 */
public class SubjectSpinnerAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {
    private static final String TAG = "SubjectSpinnerAdapter";
    private Context mContext;
    private DataWrapper<Subject> mSubjects;


    public SubjectSpinnerAdapter(Context context, DataWrapper<Subject> subjects) {
        super();
        this.mContext = context;
        this.mSubjects = subjects;
    }

    public int getPositionOfSubject(int subjectID) {
        int pos = -1;
        for(int i = 0; i < mSubjects.size(); i++) {
            if(mSubjects.get(i).getId() == subjectID) {
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
        return mSubjects.get(position).getId();
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
        View row = inflater.inflate(R.layout.listitem_subject_spinner, parent, false); //False is important. It indicates whether the view should be added directly to the ViewGroup
        TextView name = (TextView) row.findViewById(R.id.text_subject);
        View colourBar = row.findViewById(R.id.colour_bar);
        String text = this.mSubjects.get(position).getName() + ", " + this.mSubjects.get(position).getTeacher();
        name.setText(text);
        colourBar.setBackgroundColor(this.mSubjects.get(position).getColor());
        return row;
    }
}

