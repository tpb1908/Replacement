package com.anapp.tpb.replacement.Storage.TableTemplates;

/**
 * Created by Theo on 25/01/2016.
 */
public class Homework extends Task {
    private String details;
    private boolean complete;
    private int percentDone;


    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public int getPercentDone() {
        return percentDone;
    }

    public void setPercentDone(int percentDone) {
        this.percentDone = percentDone;
    }

    @Override
    public boolean equals(Object o) {
        try {
            Homework h = (Homework) o;
            if (h.getId() == getId()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
