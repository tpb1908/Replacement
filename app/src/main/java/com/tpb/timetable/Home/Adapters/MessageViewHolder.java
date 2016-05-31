package com.tpb.timetable.Home.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tpb.timetable.R;
import com.tpb.timetable.Utils.ColorResources;


/**
 * Created by theo on 30/04/16.
 */
public class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView mMessage;

    public MessageViewHolder(View v) {
        super(v);
        mMessage = (TextView) v.findViewById(R.id.text_message);
        if(ColorResources.darkTheme) {
            mMessage.setTextColor(v.getResources().getColor(R.color.colorLightText));
        } else {
            mMessage.setTextColor(v.getResources().getColor(R.color.colorPrimaryText));
        }
    }

}
