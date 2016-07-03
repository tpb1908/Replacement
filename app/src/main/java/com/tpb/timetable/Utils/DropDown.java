package com.tpb.timetable.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tpb.timetable.R;

import java.util.ArrayList;

/**
 * Created by theo on 02/07/16.
 */
public class DropDown extends PopupWindow {
    private RecyclerView mItemRecycler;
    private DropDownAdapter mItemAdaper;
    private ArrayList<DropDownItem> mRootItems; //The only ones without parents
    private ArrayList<DropDownItem> mCurrentItems; //The current st of items
    private DropDownItem mPreviousItem; //From this, we can get its parent, and then the items on the same level

    public DropDown(Context context) {
        super(context);
    }

    /**
     *Explanation for tomorrow-
     * The root views are the first ones to be added
     * Each of them may have children. If they are clicked we switch to the children
     *
     * Each also stores a reference to its parent, so that we can move back up
     * We don't need to store a reference to all of the items on the previous depth
     * as they can be found by calling getParent on the parent, and then getChildren on that
     *
     */

    public DropDown(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DropDown(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DropDown(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void itemClick(DropDownItem item) {
        if(item.hasChildren()) {
            //Get items and set views

            mPreviousItem = item;
        } else {
            if(item.getAction() != null) {
                item.getAction().run();
                //Close the dropdown
                dismiss();
            }
        }
    }

    private void moveUp(DropDownItem item) {

    }

    private void moveDown(DropDownItem item) {
        mCurrentItems = item.getChildren();
        mPreviousItem = item;
    }

    public void onBackPressed() {

    }

    @Override
    public void setContentView(View contentView) {
        //We don't allow this
    }

    public class DropDownAdapter extends RecyclerView.Adapter<DropDownItem> {

        @Override
        public DropDownItem onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DropDownItem(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.listitem_dropdown_item,
                    parent,
                    false));
        }

        @Override
        public void onBindViewHolder(DropDownItem holder, int position) {

        }

        @Override
        public int getItemCount() {
            return mCurrentItems.size();
        }
    }

    public static class DropDownItem extends RecyclerView.ViewHolder {
        private int mDepth = 0;
        private DropDownItem mParent;
        private ArrayList<DropDownItem> mChildren = new ArrayList<>();
        private FrameLayout mContainer;
        private Runnable mAction;


        public DropDownItem(@NonNull View itemView) {
            super(itemView);
            mContainer = (FrameLayout) itemView.findViewById(R.id.content_container);
        }

        public void setBasicView(TextView view) {}

        public void setView(View view) {
            mContainer.addView(view);
        }

        public void setAction(Runnable runnable) {
            mAction = runnable;
        }

        public Runnable getAction() {
            return mAction;
        }

        public void setParent(DropDownItem parent) {
            mParent = parent;
        }

        public DropDownItem getParent() {
            return mParent;
        }

        public ArrayList<DropDownItem> getChildren() {
            return mChildren;
        }

        public boolean hasChildren() {
            return mChildren.size() > 0;
        }

        public int getChildCount() {
            return mChildren.size();
        }

        public boolean updateChild(DropDownItem child) {
            final int index = mChildren.indexOf(child);
            if(index != -1) {
                mChildren.set(index, child);
                return true;
            }
            return false;
        }

        public void addChild(DropDownItem child) {
            mChildren.add(child);
        }

        public void addChild(DropDownItem child, int index) {
            mChildren.add(index, child);
        }

        public void addChildren(ArrayList<DropDownItem> children) {
            mChildren.addAll(children);
        }

        public void removeChild(DropDownItem child) {
            mChildren.remove(child);
        }

        public void removeChildren(int index) {
            mChildren.remove(index);
        }

        public void removeChildren(ArrayList<DropDownItem> children) {
            mChildren.removeAll(children);
        }

        public void clearChildren() {
            mChildren.clear();
        }

        public interface DropDownItemListener {

        }

    }


}
