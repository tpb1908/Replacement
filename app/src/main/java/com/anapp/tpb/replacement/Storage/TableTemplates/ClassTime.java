package com.anapp.tpb.replacement.Storage.TableTemplates;

import java.io.Serializable;

/**
 * Created by Theo on 19/02/2016.
 */
public class ClassTime implements Serializable {
    private int id;
    private int day;
    private int subjectID;
    private int start;
    private int end;

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

    @Override
    public String toString() {
        return "Class {id=" + id + ", SubjectId=" + subjectID + ", start=" + start + ", end=" + end + ", day=" + day + "}";
    }

    @Override
    public boolean equals(Object o) {
        try {
            ClassTime ct = (ClassTime) o;
            if (id == ct.getId()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
