package com.ctse.androidgamereviewer.data;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;

public class GameRepository {

    private GameDAO gameDAO;
    private LiveData<List<Game>> allGames;

    public GameRepository(Application application) {
        GameDatabase database = GameDatabase.getInstance(application);
        gameDAO = database.gameDAO();
        allGames = gameDAO.getAllGames();
    }

    public void insert(Game game) {
        new InsertGameAsyncTask(gameDAO).execute(game);
    }

    public void update(Game game) {
        new UpdateGameAsyncTask(gameDAO).execute(game);
    }

    public void delete(Game game) {
        new DeleteGameAsyncTask(gameDAO).execute(game);
    }

    public LiveData<List<Game>> getAllGames() {
        return allGames;
    }

    private static class InsertGameAsyncTask extends AsyncTask<Game, Void, Void> {

        private GameDAO gameDAO;

        private InsertGameAsyncTask(GameDAO gameDAO) {
            this.gameDAO = gameDAO;
        }

        @Override
        protected Void doInBackground(Game... games) {
            gameDAO.insertGame(games[0]);
            return null;
        }
    }

    private static class UpdateGameAsyncTask extends AsyncTask<Game, Void, Void> {

        private GameDAO gameDAO;

        private UpdateGameAsyncTask(GameDAO gameDAO) {
            this.gameDAO = gameDAO;
        }

        @Override
        protected Void doInBackground(Game... games) {
            gameDAO.update(games[0]);
            return null;
        }
    }

    private static class DeleteGameAsyncTask extends AsyncTask<Game, Void, Void> {

        private GameDAO gameDAO;

        private DeleteGameAsyncTask(GameDAO gameDAO) {
            this.gameDAO = gameDAO;
        }

        @Override
        protected Void doInBackground(Game... games) {
            gameDAO.delete(games[0]);
            return null;
        }
    }
}
