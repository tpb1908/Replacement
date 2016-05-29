package com.tpb.timetable.Data.Templates;

import java.io.Serializable;

/**
 * Created by theo on 27/05/16.
 */
public class Subject extends Data implements Comparable<Subject>, Serializable {
    private int id;
    private String name;
    private String teacher;
    private String classroom;
    private int color;

    public Subject(){}

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Subject) {
            Subject s = (Subject) o;
            return id == s.getID();
        }
        return false;
    }

    @Override
    public String toString() {
        return "Subject {id=" + id + ", name=" + name + ", teacher=" + teacher +
                ", classroom=" + classroom + " color=" + color +"}";
    }

    @Override
    public int compareTo(Subject another) {
        return 0;
    }
}
