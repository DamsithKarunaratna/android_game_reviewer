package com.ctse.androidgamereviewer.data.dao;

import com.ctse.androidgamereviewer.data.entities.Game;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * GameDAO includes methods that offer abstract access to the game database.
 * The room framework creates an implementation of the GameDAO at runtime.
 *
 * @see Dao
 */
@Dao
public abstract class GameDAO {

    @Insert(onConflict = REPLACE)
    public abstract void insertGame(Game game);

    @Insert(onConflict = REPLACE)
    public abstract void insertMany(List<Game> games);

    @Update
    public abstract void update(Game game);

    @Delete
    public abstract void delete(Game game);

    @Query("SELECT * FROM Game WHERE id =:id")
    public abstract Game getGame(int id);

    @Query("SELECT * FROM Game")
    public abstract LiveData<List<Game>> getAllGames();

}
