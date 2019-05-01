package com.ctse.androidgamereviewer.data;

import android.content.Context;

import com.ctse.androidgamereviewer.data.dao.GameDAO;
import com.ctse.androidgamereviewer.data.dao.ReviewDAO;
import com.ctse.androidgamereviewer.data.entities.Game;
import com.ctse.androidgamereviewer.data.entities.Review;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Singleton class to return a GameDatabase
 */
@Database(entities = {Game.class, Review.class}, version = 5, exportSchema = false)
public abstract class GameDatabase extends RoomDatabase {

    private static GameDatabase instance;

    public abstract GameDAO gameDAO();

    public abstract ReviewDAO reviewDAO();

    public static synchronized GameDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    GameDatabase.class, "game_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }

}
