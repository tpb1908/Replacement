package com.tpb.timetable.Setup.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.Term;
import com.tpb.timetable.Home.Adapters.MessageViewHolder;
import com.tpb.timetable.Home.Interfaces.AdapterManager;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.Format;
import com.tpb.timetable.Utils.UIHelper;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by theo on 25/05/16.
 */
public class TermAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DBHelper.ArrayChangeListener<Term> {
    private static final String TAG = "TermAdapter";
    private DBHelper.ArrayWrapper<Term> mTerms;
    private final ArrayList<Runnable> mQueuedUpdates = new ArrayList<>();
    private AdapterManager<Term> mManager;
    private Context mContext;
    private boolean mWasEmpty;

    public TermAdapter(Context context, AdapterManager<Term> manager) {
        mContext = context;
        mManager = manager;
        mTerms = DBHelper.getInstance(context).getAllTerms();
        mTerms.addListener(this);
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
            final String DATERANGE = Format.dateToString(new Date(term.getStartDate())) +
                    " to " + Format.dateToString(new Date(term.getEndDate()));
            termHolder.mTermName.setText(term.getName());
            termHolder.mDateRange.setText(DATERANGE);
            Log.i(TAG, "onBindViewHolder: Creating term viewholder");
        } else {
            final MessageViewHolder mv = (MessageViewHolder) holder;
            mv.setMessage(mContext.getResources().getString(R.string.message_no_terms_created));
            Log.i(TAG, "onBindViewHolder: Creating message viewholder");
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
        mManager.removed(mTerms.get(index));
        mTerms.remove(index);
        runQueuedUpdates();
    }

    public int numTerms() {
        return mTerms.size();
    }

    //Interface methods
    @Override
    public int getItemCount() {
        if(mTerms.size() == 0) {
            mWasEmpty = true;
            return 1;
        } else {
          mWasEmpty = false;
            return mTerms.size();
        }
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
    public void add(final int index, Term term) {
        mQueuedUpdates.add(new Runnable() {
            @Override
            public void run() {
                if(mWasEmpty) {
                    notifyItemChanged(0);
                } else {
                    notifyItemInserted(index);
                }
            }
        });
    }

    @Override
    public void add(Term term) {
        add(mTerms.indexOf(term), term);
    }

    @Override
    public void set(final int index, Term term) {
        mQueuedUpdates.add(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(index);
            }
        });
    }

    @Override
    public void moved(int oldIndex, int newIndex) {
        notifyItemMoved(oldIndex, newIndex);
        notifyItemChanged(newIndex);
    }

    @Override
    public void updated(final int index, Term term) {
        mQueuedUpdates.add(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(index);
            }
        });
    }

    @Override
    public void removed(final int index, Term term) {
        Log.i(TAG, "removed: Received notification of removed term");
        mQueuedUpdates.add(new Runnable() {
            @Override
            public void run() {
                notifyItemRemoved(index);
            }
        });
    }

    @Override
    public void cleared() {
        notifyDataSetChanged();
    }


    //Term ViewHolder
    public static class TermViewHolder extends RecyclerView.ViewHolder {
        private TextView mTermName;
        private TextView mDateRange;

        public TermViewHolder(final View itemView, final TermAdapter parent) {
            super(itemView);
            mTermName = (TextView) itemView.findViewById(R.id.text_term_name);
            mDateRange = (TextView) itemView.findViewById(R.id.text_term_date_range);
            final ImageButton mDeleteButton = (ImageButton) itemView.findViewById(R.id.button_delete);
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.deleteTerm(getAdapterPosition());
                }
            });
            final ImageView mIcon = (ImageView) itemView.findViewById(R.id.icon_term);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.mManager.open(
                            parent.mTerms.get(getAdapterPosition()),
                            itemView);
                }
            });
            UIHelper.theme((ViewGroup) itemView);
        }
    }

}
