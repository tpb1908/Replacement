package com.anapp.tpb.replacement.Home.Utilities;

import com.anapp.tpb.replacement.Home.Interfaces.DataUpdateListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by theo on 19/05/16.
 */
public class DataWrapper<T extends Comparable<T>> implements Serializable {
    private ArrayList<T> data;
    private ArrayList<DataUpdateListener<T>> listeners;

    public DataWrapper() {
        data = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public DataWrapper(DataUpdateListener<T>... initialListeners) {
        data = new ArrayList<>();
        listeners = new ArrayList<>();
        Collections.addAll(listeners, initialListeners);

    }

    public void addListener(DataUpdateListener<T> newListener) {
        if(!listeners.contains(newListener)) listeners.add(newListener);
    }

    public void removeListener(DataUpdateListener<T> listener) {
        listeners.remove(listener);
    }

    private void clearNullListeners() {
        listeners.removeAll(Collections.singleton(null));
    }

    public void sort() {
        Collections.sort(data);
    }

    //Boolean methods
    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int size() {
        return data.size();
    }

    public boolean contains(T t) {
        return data.contains(t);
    }

    //Indexers
    public int indexOf(T t) {
        return data.indexOf(t);
    }

    public int lastIndexOf(T t) {
        return data.lastIndexOf(t);
    }


    //Get
    public T get(int index) {
        return data.get(index);
    }

    public Object[] toArray() {
        return data.toArray();
    }

    public Iterator<T> iterator() {
        return data.iterator();
    }

    //Setters/adders
    public void set(int index, T t) {
        data.set(index, t);
    }

    public void setData(ArrayList<T> toSet) {
        data = toSet;
        clearNullListeners();
        for(DataUpdateListener<T> listener : listeners) {
            listener.updateAll();
        }
    }

    public void addAll(ArrayList<T> toAdd) {
        data.addAll(toAdd);
        //TODO- Add another method?
        clearNullListeners();
        for(DataUpdateListener<T> listener : listeners) {
            listener.updateAll();
        }
    }

    public void add(T t) {
        data.add(t);
        clearNullListeners();
        for(DataUpdateListener<T> listener : listeners) {
            listener.add(t);
        }
    }

    public boolean add(int index, T t) {
        if(index < 0 || index >= data.size()) {
            return false;
        }
        data.add(index, t);
        clearNullListeners();
        for(DataUpdateListener<T> listener : listeners) {
            listener.add(index, t);
        }
        return true;
    }

    //Manipulators
    public boolean move(int oldPos, int newPos) {
        if(oldPos < 0 || newPos < 0 ||oldPos >= data.size() || newPos >= data.size()) {
            return false;
        }
        T toMove = data.get(oldPos);
        data.remove(toMove);
        if(oldPos > newPos) {
            data.add(newPos, toMove);
        } else {
            /*If the old index is to the left of the new index, the new index
                has been effectively shifted one to the left
             */
            data.add(newPos-1, toMove);
        }
        clearNullListeners();
        for(DataUpdateListener<T> listener : listeners) {
            listener.move(toMove, oldPos, newPos);
        }
        return true;
    }

    public boolean update(T t) {
        int index = data.indexOf(t);
        if(index != -1) {
            data.set(index, t);
            clearNullListeners();
            for(DataUpdateListener<T> listener : listeners) {
                listener.update(t);
            }
            return true;
        }
        return false;
    }

    //Removers
    public boolean remove(T t) {
        boolean isRemoved = data.remove(t);
        for(DataUpdateListener<T> listener : listeners) {
            listener.remove(t);
        }
        return isRemoved;
    }

    public boolean remove(int index) {
        if(index < data.size()) {
            T t = data.get(index);
            data.remove(index);
            clearNullListeners();
            for(DataUpdateListener<T> listener : listeners) {
                listener.remove(index, t);
            }
            return true;
        }
        return false;
    }

    public void clear() {
        data.clear();
        clearNullListeners();
        for(DataUpdateListener<T> listener : listeners) {
            listener.updateAll();
        }
    }
}
