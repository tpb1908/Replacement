package com.tpb.timetable.Setup.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.Term;
import com.tpb.timetable.Home.Adapters.MessageViewHolder;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.FormattingUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by theo on 25/05/16.
 */
public class TermAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DBHelper.ArrayChangeListener<Term> {
    private static final String TAG = "TermAdapter";
    private DBHelper.ArrayWrapper<Term> mTerms;
    private final ArrayList<Runnable> mQueuedUpdates = new ArrayList<>();
    private Context mContext;

    public TermAdapter(Context context) {
        this.mContext = context;
        mTerms = DBHelper.getInstance(context).getAllTerms();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0) {
            final View v = LayoutInflater.from(mContext).inflate(R.layout.listitem_no_data_message, parent, false);
            return new MessageViewHolder(v);
        } else {
            final View v = LayoutInflater.from(mContext).inflate(R.layout.listitem_term, parent, false);
            return new TermViewHolder(v, this);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == 1) {
            final TermViewHolder termHolder = (TermViewHolder) holder;
            final Term term = mTerms.get(position);
            final String DATERANGE = FormattingUtils.dateToString(new Date(term.getStartDate())) +
                    " to " + FormattingUtils.dateToString(new Date(term.getEndDate()));
            termHolder.mTermName.setText(term.getName());
            termHolder.mDateRange.setText(DATERANGE);
        } else {
            final MessageViewHolder mv = (MessageViewHolder) holder;
            mv.setMessage(mContext.getResources().getString(R.string.message_no_terms_setup));
        }
    }

    public void runQueuedUpdates() {
        if(mQueuedUpdates.size() > 3) {
            notifyDataSetChanged();
        } else {
            for(Runnable r : mQueuedUpdates) r.run();
        }
        mQueuedUpdates.clear();
    }

    public void deleteTerm(int index) {
        final Term mDeletedTerm = mTerms.get(index);
        mTerms.remove(index);
        runQueuedUpdates();
    }

    public int numTerms() {
        return mTerms.size();
    }

    public void open(int index) {}

    //Interface methods
    @Override
    public int getItemCount() {
        return mTerms.size() > 0 ? mTerms.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        return mTerms.size() > 0 ? 1 : 0;
    }

    @Override
    public void dataSetChanged() {
        notifyDataSetChanged();
    }

    @Override
    public void dataSorted() {
        notifyDataSetChanged();
    }

    @Override
    public void add(int index, Term term) {
        notifyItemInserted(index);
    }

    @Override
    public void add(Term term) {
        notifyItemInserted(mTerms.indexOf(term));
    }

    @Override
    public void set(int index, Term term) {
        notifyItemChanged(index);
    }

    @Override
    public void moved(int oldIndex, int newIndex) {
        notifyItemMoved(oldIndex, newIndex);
    }

    @Override
    public void updated(int index, Term term) {
        notifyItemChanged(index);
    }

    @Override
    public void removed(int index, Term term) {
        notifyItemRemoved(index);
    }

    @Override
    public void cleared() {
        notifyDataSetChanged();
    }


    //Term ViewHolder
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
                    parent.deleteTerm(getAdapterPosition());
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
