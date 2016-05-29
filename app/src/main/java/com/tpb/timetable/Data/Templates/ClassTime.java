package com.tpb.timetable.Data.Templates;

import java.io.Serializable;

/**
 * Created by theo on 27/05/16.
 */
public class ClassTime extends Data implements Comparable<ClassTime>, Serializable {
    private int id = -1;
    private int day;
    private int subjectID;
    private int startTime;
    private int endTime;
    private Subject subject;

    public ClassTime() {}

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        this.subjectID = subject.getID();
    }

    @Override
    public int compareTo(ClassTime another) {
        return startTime >= another.getStartTime() ? 1 : -1;
    }

    public boolean overlaps(ClassTime toCheck) {
        return (startTime < toCheck.getEndTime() && endTime > toCheck.getStartTime());
    }

    @Override
    public String toString() {
        String string = "Class {id=" + id + ", SubjectId=" + subjectID + ", start=" + startTime +
                ", end=" + endTime+ ", day=" + day;
        if(subject != null) string += ", subject " + subject.toString();
        return string + "}";
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ClassTime) {
            ClassTime ct = (ClassTime) o;
            return  id == ct.getID();
        }
        return false;
    }
}
