package com.anapp.tpb.replacement.Storage.TableTemplates;

import java.io.Serializable;

/**
 * Created by Theo on 25/01/2016.
 */
public class Subject implements Serializable, Comparable<Subject> {

    private int id;
    private String name;
    private String teacher;
    private String classroom;
    private int color;

    public Subject() {
    }

    public Subject(String name, String teacher, String classroom) {
        this.name = name;
        this.teacher = teacher;
        this.classroom = classroom;
    }

    @Override
    public String toString() {
        return "Subject {id=" + id + ", name=" + name + ", teacher=" + teacher + ", classroom=" + classroom + " color=" + color +"}";
    }

    @Override
    public boolean equals(Object o) {
        try {
            Subject l = (Subject) o;
            return l.getId() == id;
        } catch (Exception e) {
            return false;
        }
    }

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
    public int compareTo(Subject another) {
        return 0;
    }
}
