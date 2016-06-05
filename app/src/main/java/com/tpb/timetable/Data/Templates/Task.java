package com.tpb.timetable.Data.Templates;

import java.io.Serializable;

/**
 * Created by theo on 27/05/16.
 */
public class Task extends Data implements Comparable<Task>, Serializable {
    private int id;
    private int type;
    private String title;
    private String detail;
    private long startDate;
    private long endDate;
    private long dateComplete;
    private boolean showReminder;
    private int subjectID;
    private int time;
    private boolean complete;
    private int percentageComplete;
    private Subject subject;

    public Task(int type) {
        this.type = type;
    }

    public Task() {}

    public String getTypeName() {
        switch(type) {
            case 0:
                return "Task";
            case 1:
                return "Homework";
            case 2:
                return "Reminder";
            default:
                return "";
        }
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
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
        this.detail = detail.trim();
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

    public boolean getShowReminder() {
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

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        this.subjectID = subject.getID();
    }

    public long getDateComplete() {
        return dateComplete;
    }

    public void setDateComplete(long dateComplete) {
        this.dateComplete = dateComplete;
    }

    @Override
    public int compareTo(Task another) {
        if(complete) { //This task is complete,
            if(another.complete) { //If they are both complete, we compare times
                return endDate > another.endDate ? 1 : -1;
            } else { //If the other task isn't complete, it has higher precedence
                return -1;
            }
        } else { //The opposite
            if(another.complete) {
                return 1;
            } else {
                return endDate > another.endDate ? 1 : -1;
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if(o instanceof Task) {
            final Task t = (Task) o;
            return id == t.id;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Task {id="+id+", type="+type+", title="+title+ ", detail=" + detail+
                ", startDate=" +startDate+", endDate="+endDate+", reminder="+showReminder+
                ", subjectID=" +subjectID+", time="+time+", isComplete="+ complete +
                ", dateComplete="+ dateComplete +", percentageComplete=" + percentageComplete + " }";
    }
}
