package com.anapp.tpb.replacement.Storage.TableTemplates;

import java.util.ArrayList;

/**
 * Created by pearson-brayt15 on 27/01/2016.
 */
public class Review extends Task {
    private String details;
    private ArrayList<String> updates;
    private boolean complete;

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public ArrayList<String> getUpdates() {
        return updates;
    }

    public void setUpdates(ArrayList<String> updates) {
        this.updates = updates;
    }

    @Override
    public boolean equals(Object o) {
        try {
            Review r = (Review) o;
            if (r.getId() == getId()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
