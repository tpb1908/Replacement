package com.anapp.tpb.replacement.Home.Interfaces;

/**
 * Created by theo on 19/05/16.
 */
public interface DataUpdateListener<T> {

    void updateAll();

    void add(T t);

    void add(int index, T t);

    void remove(int index, T t);

    void update(T t);

    void move(T t, int oldPos, int newPos);

}
