package com.anapp.tpb.replacement.Setup.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anapp.tpb.replacement.Home.Utilities.MessageViewHolder;
import com.anapp.tpb.replacement.Home.Utilities.TimeUtils;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.Term;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by theo on 25/05/16.
 */
public class TermAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "TermAdapter";
    private ArrayList<Term> mTerms;
    private DataHelper mDataHelper;
    private Context mContext;

    public TermAdapter(Context context) {
        this.mContext = context;
        mDataHelper = DataHelper.getInstance(context);
        mTerms = mDataHelper.getAllTerms();
    }

    public void add(Term term) {
        term = mDataHelper.addTerm(term);
        int pos;
        for(pos = 0; pos < mTerms.size(); pos++) {
            if(mTerms.get(pos).getStartDate() > term.getEndDate()) break;
        }
        mTerms.add(pos, term);
        notifyItemInserted(pos);
    }

    public void update(Term term) {
        int pos = mTerms.indexOf(term);
        if(pos != -1) {
            mTerms.set(pos, term);
            Collections.sort(mTerms);
            int newPos = mTerms.indexOf(term);
            if(newPos == pos) {
                notifyItemChanged(pos);
            } else {
                notifyItemMoved(pos, newPos);
            }
        } else {
            Log.i(TAG, "Something went wrong when updating " + term.toString());
        }
    }

    public void remove(int index) {
        mTerms.remove(index);
        notifyItemRemoved(index);
    }

    public void open(int index) {}


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.listitem_no_data_message, parent, false);
            return new MessageViewHolder(v);
        } else {
            View v = LayoutInflater.from(mContext).inflate(R.layout.listitem_term, parent, false);
            return new TermViewHolder(v, this);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == 1) {
            TermViewHolder termHolder = (TermViewHolder) holder;
            Term term = mTerms.get(position);
            final String DATERANGE = TimeUtils.getDateString(new Date(term.getStartDate())) +
                    " to " + TimeUtils.getDateString(new Date(term.getEndDate()));
            termHolder.mTermName.setText(term.getName());
            termHolder.mDateRange.setText(DATERANGE);
        } else {
            MessageViewHolder mv = (MessageViewHolder) holder;
            mv.mMessage.setText(mContext.getResources().getString(R.string.message_no_terms_setup));
        }

    }

    @Override
    public int getItemCount() {
        return mTerms.size() > 0 ? mTerms.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        return mTerms.size() > 0 ? 1 : 0;
    }

    public static class TermViewHolder extends RecyclerView.ViewHolder {
        private TextView mTermName;
        private TextView mDateRange;
        private ImageButton mDeleteButton;

        public TermViewHolder(View itemView, final TermAdapter parent) {
            super(itemView);
            mTermName = (TextView) itemView.findViewById(R.id.text_term_name);
            mDateRange = (TextView) itemView.findViewById(R.id.text_term_date_range);
            mDeleteButton = (ImageButton) itemView.findViewById(R.id.button_delete);
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.remove(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.open(getAdapterPosition());
                }
            });
        }
    }

}
