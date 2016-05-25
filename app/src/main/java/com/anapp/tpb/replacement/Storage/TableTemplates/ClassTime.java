package com.anapp.tpb.replacement.Storage.TableTemplates;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Theo on 19/02/2016.
 */
public class ClassTime extends DataTemplate implements Serializable{
    private int id;
    private int day;
    private int subjectID;
    private int start;
    private int end;
    private Subject subject;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Subject getSubject() { return subject; }

    public void setSubject(Subject subject) { this.subject = subject; }

    /**
     * Checks whether a class occurs after another, ignoring day
     * @param another The class to compare to
     * @return 1 if the class starts after the argument class ends, -1 otherwise
     */
    @Override
    public int compareTo (@NonNull DataTemplate another) {
        if(another instanceof ClassTime) {
            ClassTime ct = (ClassTime) another;
            return start >= ct.getEnd() ? 1 : -1;
        }
        return 0;
    }

    /**
     * Checks if a class overlaps another. Does not check day
     * @param toCheck Another ClassTime with which to find an overlap
     * @return True if the classes overlap, false if they don't
     */
    public boolean overlaps(ClassTime toCheck) {
        return (start < toCheck.getEnd() && end > toCheck.getStart());
    }

    @Override
    public String toString() {
        String string = "Class {id=" + id + ", SubjectId=" + subjectID + ", start=" + start + ", end=" + end + ", day=" + day;
        if(subject != null) string += ", subject " + subject.toString();
        return string + "}";
    }

    /**
     * Checks if a class is equal to another
     * @param o The object to compare
     * @return True if the object can be cast to a ClassTime with the same id as the class
     */
    @Override
    public boolean equals(Object o) {
        try {
            ClassTime ct = (ClassTime) o;
            return id == ct.getId();
        } catch (Exception e) {
            return false;
        }
    }
}
