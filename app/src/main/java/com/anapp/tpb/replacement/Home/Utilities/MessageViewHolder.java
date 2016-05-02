package com.anapp.tpb.replacement.Home.Utilities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.anapp.tpb.replacement.R;

/**
 * Created by theo on 30/04/16.
 */
public class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView mMessage;

    public MessageViewHolder(View v) {
        super(v);
        mMessage = (TextView) v.findViewById(R.id.text_message);
    }

}
