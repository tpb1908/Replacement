package com.anapp.tpb.replacement.Setup.Adapters;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Setup.DataCollection.TermInput;
import com.anapp.tpb.replacement.Setup.DataPresentation.TermDateCollector;
import com.anapp.tpb.replacement.Storage.TableTemplates.Term;
import com.anapp.tpb.replacement.Storage.StorageHelpers.TermStorageHelper;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Theo on 23/01/2016.
 */
public class TermListAdapter extends RecyclerView.Adapter<TermListAdapter.ViewHolder> {
    private ArrayList<Term> terms;
    private TermStorageHelper storageHelper;
    private TermDateCollector parent;

    public TermListAdapter(TermDateCollector parent, TermStorageHelper storageHelper) {
        this.parent = parent;
        this.storageHelper = storageHelper;
        terms = storageHelper.getAllTerms();
    }

    public void addItem(Term term) {
        term = storageHelper.addTerm(term);
        terms.add(term);
        notifyItemInserted(getItemCount());
    }

    public void delete(int position) {
        storageHelper.delete(terms.get(position));
        terms.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Layout inflater comes from parent
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.term_date_listitem, parent, false);
        ViewHolder vh = new ViewHolder(v, this);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Adapter position- ", "" + holder.getAdapterPosition());
                delete(holder.getAdapterPosition());

            }
        });
        holder.termNameTextView.setText(terms.get(position).getName());
        String dateRange;
        Date start = new Date(terms.get(holder.getAdapterPosition()).getStartDate());
        Date end = new Date(terms.get(holder.getAdapterPosition()).getEndDate());
        dateRange = start.toString().substring(0, 10) + " to " + end.toString().substring(0, 10);
        holder.termDateRangeTextView.setText(dateRange);
        Log.d("Term binding", terms.get(position).toString() + "    " + start.toString() + "     " + end.toString());
    }

    private void updateTerm(int position) {
        Intent i = new Intent(parent.getApplicationContext(), TermInput.class);
        i.putExtra("terms", terms);
        i.putExtra("editingTerm", terms.get(position));
        parent.startActivityForResult(i, 1);

    }

    public ArrayList<Term> getTerms() {
        return terms;
    }

    public void updateTermValue(Term t) {
        terms.set(terms.indexOf(t), t);
        storageHelper.update(t);
        notifyItemChanged(terms.indexOf(t));
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView termNameTextView;
        public TextView termDateRangeTextView;
        public ImageButton deleteButton;
        private TermListAdapter parent;

        public ViewHolder(View v, TermListAdapter p) {
            super(v);
            this.parent = p;
            termNameTextView = (TextView) v.findViewById(R.id.term_date_list_name_textview);
            termDateRangeTextView = (TextView) v.findViewById(R.id.term_date_list_date_range_textview);
            deleteButton = (ImageButton) v.findViewById(R.id.deleteButton);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ViewHolder", "Click detected");
                    parent.updateTerm(getAdapterPosition());
                }
            });
        }
    }
}
