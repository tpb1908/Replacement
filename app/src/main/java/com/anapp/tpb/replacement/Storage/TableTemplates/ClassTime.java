package com.anapp.tpb.replacement.Storage.TableTemplates;

import java.io.Serializable;

/**
 * Created by Theo on 19/02/2016.
 */
public class ClassTime implements Serializable {
    private int id;
    private int SubjectID;
    private long start;
    private long end;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubjectID() {
        return SubjectID;
    }

    public void setSubjectID(int ClassID) {
        ClassID = ClassID;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    @Override
    public String toString() {
        return "Class {id=" + id + ", SubjectId=" + SubjectID + ", start=" + start + ", end=" + end + "}";
    }
}
