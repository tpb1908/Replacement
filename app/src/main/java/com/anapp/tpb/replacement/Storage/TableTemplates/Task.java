package com.anapp.tpb.replacement.Storage.TableTemplates;

/**
 * Created by Theo on 25/01/2016.
 */
public class Task {

    //Types of task: 0- Homework, 1-General task, 2-Reminder

    private int id; //ALL
    private int type;  //Homework, general task, reminder
    private String title; //All
    private String detail; //ALL
    private long startDate; //ALL
    private long endDate; //ALL
    private boolean showReminder; //ALL
    private int subjectID; //Homework, general task
    private int time; //note
    private boolean complete; //Homework, general task
    private int percentageComplete; //Homework

    public Task() {}

    public Task(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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

    public boolean showReminder() {
        return showReminder;
    }

    public void setShowReminder(boolean showReminder) {
        this.showReminder = showReminder;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public int getPercentageComplete() {
        return percentageComplete;
    }

    public void setPercentageComplete(int percentageComplete) {
        this.percentageComplete = percentageComplete;
    }

    @Override
    public String toString() {
        return "Task {id="+id+", type="+type+", title="+title+ ", detail=" + detail+
                ", startDate=" +startDate+", endDate="+endDate+", reminder="+showReminder+
                ", subjectID=" +subjectID+", time="+time+", isComplete="+isComplete()+
                "percentageComplete="+percentageComplete+" }";
    }
}
