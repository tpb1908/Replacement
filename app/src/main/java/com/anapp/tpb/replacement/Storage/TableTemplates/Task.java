package com.anapp.tpb.replacement.Storage.TableTemplates;

/**
 * Created by Theo on 25/01/2016.
 */
public abstract class Task {

    private int id;
    private String name;
    private long startDate;
    private long endDate;
    private boolean showReminder;
    private int sessionID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public boolean isShowReminder() {
        return showReminder;
    }

    public void setShowReminder(boolean showReminder) {
        this.showReminder = showReminder;
    }

}
