package com.anapp.tpb.replacement.Storage.TableTemplates;

import android.support.annotation.NonNull;

/**
 * Created by Theo on 25/01/2016.
 */
public class Task implements Comparable<Task> {

    //Types of task: 1- Task, 2-Homework, 3-Reminder

    private int id; //ALL
    private int type;  //Homework, general task, reminder
    private String title; //All
    private String detail; //ALL
    private long startDate; //ALL
    private long endDate; //ALL
    private long completeDate; //Task, homework
    private boolean showReminder; //ALL, true on reminder
    private int subjectID; //Homework, general task
    private int time; //note
    private boolean complete; //Homework, general task
    private int percentageComplete; //Homework
    private Subject subject;

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

    public long getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(long completeDate) {
        this.completeDate = completeDate;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Override
    public boolean equals (Object o) {
        try {
            Task t = (Task) o;
            return t.getId() == id;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int compareTo(@NonNull Task another) {
        //Complete and non-complete tasks probably won't be compared, however there is still a check
        if(complete) { //This task is complete
            if(another.complete) { //If they are both complete, compare times
                return startDate > another.startDate ? 1 : -1;
            } else { //If the other task isn't complete, it has higher precedence
                return -1;
            }
        } else { //The opposite
            if(another.complete) {
                return 1;
            } else {
                return startDate > another.startDate ? 1 : -1;
            }
        }
    }

    @Override
    public String toString() {
        return "Task {id="+id+", type="+type+", title="+title+ ", detail=" + detail+
                ", startDate=" +startDate+", endDate="+endDate+", reminder="+showReminder+
                ", subjectID=" +subjectID+", time="+time+", isComplete="+isComplete()+
                ", percentageComplete=" + percentageComplete + " }";
    }
}
