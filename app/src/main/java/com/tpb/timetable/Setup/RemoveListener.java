package com.tpb.timetable.Setup;

import com.tpb.timetable.Data.Templates.Data;

/**
 * Created by theo on 04/06/16.
 */
public interface RemoveListener<T extends Data> {

    void removed(T t);

}
