package com.ctse.androidgamereviewer.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class GameDAO {

    @Insert(onConflict = REPLACE)
    public abstract void insertGame(Game game);

    @Update
    public abstract void update(Game game);

    @Delete
    public abstract void delete(Game game);

    @Query("SELECT * FROM Game WHERE id =:id")
    public abstract Game getGame(int id);

    @Query("SELECT * FROM Game")
    public abstract LiveData<List<Game>> getAllGames();

}
