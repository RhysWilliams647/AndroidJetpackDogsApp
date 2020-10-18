package com.rhys.dogsapp.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface IDogDao {
    @Insert
    List<Long> insertAll(DogBreed... dogs);

    @Query("SELECT * FROM dogbreed")
    List<DogBreed> getAll();

    @Query("SELECT * FROM dogbreed WHERE uuid = :dogId")
    DogBreed get(int dogId);

    @Query("DELETE FROM dogbreed")
    void deleteAll();
}
