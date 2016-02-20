package com.anapp.tpb.replacement.Storage.TableTemplates;

import java.io.Serializable;

/**
 * Created by Theo on 25/01/2016.
 */
public class Term implements Serializable {
    private int id;
    private String name;
    private long startDate;
    private long endDate;

    public Term() {
    }

    public Term(String name, long startDate, long endDate) {
        super();
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    @Override
    public String toString() {
        return "Term {id=" + id + ", name=" + name + ", startDate=" + startDate + ", endDate=" + endDate + "}";
    }

    @Override
    public boolean equals(Object o) {
        try {
            Term t = (Term) o;
            if (t.getId() == id) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
