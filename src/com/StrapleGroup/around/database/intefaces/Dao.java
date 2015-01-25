package com.StrapleGroup.around.database.intefaces;

import java.util.List;

public interface Dao<T> {
    long save(T type);

    void update(T type);

    void updatePhoto(T type);


    void delete(String id);

    void saveRequest(T type);

//    long saveLoginOnly(T type);

    List<T> getAll();
}
