package com.tpb.timetable.Home.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tpb.timetable.R;
import com.tpb.timetable.Utils.ThemeHelper;


/**
 * Created by theo on 30/04/16.
 */
public class MessageViewHolder extends RecyclerView.ViewHolder {
    private TextView mMessage;

    public MessageViewHolder(View v) {
        super(v);
        mMessage = (TextView) v.findViewById(R.id.text_message);
        mMessage.setTextColor(ThemeHelper.getPrimaryText());
    }

    public void setMessage(String message) {
        mMessage.setText(message);
    }

    public void setMessage(int resID) {
        mMessage.setText(resID);
    }

}
