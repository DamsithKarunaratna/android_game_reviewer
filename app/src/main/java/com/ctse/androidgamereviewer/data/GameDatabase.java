/*
 * CTSE Android Project - Game Reviewer
 * @author IT16037434 Karunaratne D. C.
 * @author IT15146366 Hettiarachchi H. A. I. S.
 *
 * File: GameDatabase.java
 */
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
 * Singleton class which returns an instance of GameDatabase using the Room databaseBuilder class.
 * GameDatabase adds another layer of abstraction to data persistence and holds the Data Access
 * Objects.
 *
 * @see Database
 * <a href="https://developer.android.com/topic/libraries/architecture/room">
 * Official Room Documentation</a>
 */
@Database(entities = {Game.class, Review.class}, version = 7, exportSchema = false)
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
