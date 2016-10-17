package com.tpb.timetable.Data.Templates;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by theo on 27/05/16.
 */
public class Subject extends Data implements Comparable<Subject>, Serializable {
    private int id;
    private String name;
    private String teacher;
    private String classroom;
    private int color;
    private String[] topics = new String[] {};

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

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }

    public void setTopicsFromString(String topicString) {
        try {
            final JSONArray jarray = new JSONObject(topicString).optJSONArray("TOPICS");
            topics = new String[jarray.length()];
            for(int i = 0; i < jarray.length(); i++) {
                topics[i] = jarray.optString(i);
            }
        } catch(JSONException je) {}

    }

    public String getTopicString() {
        final JSONObject json = new JSONObject();
        try {
            json.put("TOPICS", new JSONArray(topics));
        } catch(JSONException je) {}
        return json.toString();
    }

    public int topicIndex(String t) {
        for(int i = 0; i < topics.length; i++) {
            if(topics[i].equals(t)) return i;
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Subject && id == ((Subject) o).id;
    }

    @Override
    public String toString() {
        return "Subject {id=" + id + ", name=" + name + ", teacher=" + teacher +
                ", classroom=" + classroom + " color=" + color + ", topics=" + Arrays.toString(topics) + "}";
    }

    @Override
    public int compareTo(@NonNull Subject another) {
        return 0;
    }
}
