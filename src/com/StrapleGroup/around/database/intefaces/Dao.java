package com.StrapleGroup.around.database.intefaces;

import java.util.List;

public interface Dao<T> {
    long save(T type);

    void update(T type);

    void updateCoordinates(T type, String id);

    void delete(T type);

    List<T> getAll();
}
