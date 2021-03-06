package com.tpb.timetable.Data.Templates;

import android.support.annotation.NonNull;

import com.tpb.timetable.Utils.Format;

import java.io.Serializable;

/**
 * Created by theo on 27/05/16.
 */
public class ClassTime extends Data implements Comparable<ClassTime>, Serializable {
    private int id = -1;
    private int day = -1;
    private int subjectID;
    private int startTime = -1;
    private int endTime = -1;
    private Subject subject;
    private String topic = "";

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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public int compareTo(@NonNull ClassTime another) {
        return day >= another.day && startTime >= another.startTime ? 1 : -1;
    }

    public boolean overlaps(ClassTime toCheck) {
        return day == toCheck.day && startTime < toCheck.endTime && endTime > toCheck.startTime;
    }

    @Override
    public String toString() {
        String string = "Class {id=" + id + ", SubjectId=" + subjectID + ", start=" + startTime +
                " " + Format.format(startTime) + ", end=" + endTime +
                " " + Format.format(endTime) + ", day=" + day + ", topic=" + topic;
        if(subject != null) string += ", subject " + subject.toString();
        return string + "}";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ClassTime && id == ((ClassTime) o).id;
    }
}
