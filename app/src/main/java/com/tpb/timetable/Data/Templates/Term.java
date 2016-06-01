package com.tpb.timetable.Data.Templates;

import java.io.Serializable;

/**
 * Created by theo on 27/05/16.
 */
public class Term extends Data implements Comparable<Term>, Serializable {
    private int id = -1;
    private String name;
    private long startDate;
    private long endDate;

    public Term() {}

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

    @Override
    public int compareTo(Term another) {
        if(startDate > another.endDate) {
            return 1;
        } else if(startDate ==  another.endDate) {
            return 0;
        } else {
            return -1;
        }
    }

    public boolean overlaps(Term toCheck) {
        return (startDate < toCheck.endDate && endDate > toCheck.startDate);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Term) {
            Term t = (Term) o;
            return id == t.id;
        }
        return false;
    }
}
