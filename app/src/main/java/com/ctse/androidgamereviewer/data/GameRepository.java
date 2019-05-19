/*
 * CTSE Android Project - Game Reviewer
 * @author IT16037434 Karunaratne D. C.
 * @author IT15146366 Hettiarachchi H. A. I. S.
 *
 * File: GameRepository.java
 */
package com.ctse.androidgamereviewer.data;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.ctse.androidgamereviewer.data.dao.GameDAO;
import com.ctse.androidgamereviewer.data.entities.Game;
import com.ctse.androidgamereviewer.data.retrofit.GameWebService;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The Repository class is the highest level of abstraction for persistent data operations.
 * It acts as a mediator between the local Room-SQLite-database and the remote MongoDB database.
 * It provides a clean API so that the rest of the app can create update delete and retrieve data
 * easily without worrying about handling multiple databases.
 * <p>
 * See <a href="https://developer.android.com/jetpack/docs/guide">
 * Official android architecture guide</a> for more information.
 */
public class GameRepository {

    /**
     * GameDAO is Room Data access object for handling local DB operations
     *
     * @see androidx.room.Dao
     */
    private GameDAO gameDAO;
    /**
     * GameWebservice Retrofit webservice for handling remote DB  operations
     */
    private GameWebService webService;
    /**
     * Executor For handling concurrent tasks
     */
    private Executor executor;
    /**
     * GameDatabase is a Room Database class which creates instances of Data Access Objects
     */
    private GameDatabase database;

    /**
     * List of games is stored with the LiveData wrapper. LiveData follows the Observer pattern
     * and notifies the View whenever the data changes. LiveData is lifecycle aware and hence will
     * not update Observers which are in an inactive state.
     * <p>
     * See <a href="https://developer.android.com/topic/libraries/architecture/livedata">
     * LiveData Documentation</a> for more information.
     *
     * @see LiveData
     */
    private LiveData<List<Game>> allGames;

    public GameRepository(Application application) {
        database = GameDatabase.getInstance(application);
        gameDAO = database.gameDAO();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ctse-test-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webService = retrofit.create(GameWebService.class);
        executor = Executors.newSingleThreadExecutor();
        /*
          getAllGames() returns data wrapped in a LiveData object which can directly be assigned to
          the allGames List.
         */
        allGames = gameDAO.getAllGames();

    }

    /**
     * inserts a new game object into the database. Game is inserted into the local database as
     * well as the remote database. To avoid blocking the main thread, database operations are
     * carried out on separate threads. This enables a smooth user experience.
     *
     * @param game entity object to be persisted
     */
    public void insert(final Game game) {
        // Insert game into local SQLite Database asynchronously
        new InsertGameAsyncTask(gameDAO).execute(game);

        // Thread to insert game into remote MongoDB database asynchronously
        executor.execute(new Runnable() {
            @Override
            public void run() {
                webService.saveGame(game).enqueue(new Callback<Game>() {
                    @Override
                    public void onResponse(Call<Game> call, Response<Game> response) {
                        Log.d("gameApp", "Game saved to online DB");
                    }

                    @Override
                    public void onFailure(Call<Game> call, Throwable t) {
                        Log.d("gameApp", "Game not saved");
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    public void update(final Game game) {
        // local update
        new UpdateGameAsyncTask(gameDAO).execute(game);

        // remote update
        executor.execute(new Runnable() {
            @Override
            public void run() {
                webService.updateGame(game.get_id(), game).enqueue(new Callback<Game>() {
                    @Override
                    public void onResponse(Call<Game> call, Response<Game> response) {
                        Log.d("gameApp", "Game updated on remote DB");
                    }

                    @Override
                    public void onFailure(Call<Game> call, Throwable t) {
                        Log.d("gameApp", "Game not updated");
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    public void delete(Game game) {
        new DeleteGameAsyncTask(gameDAO).execute(game);
    }

    /**
     * @return allGames : Observable list of games.
     */
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

    private static class InsertAllGameAsyncTask extends AsyncTask<List<Game>, Void, Void> {

        private GameDAO gameDAO;

        private InsertAllGameAsyncTask(GameDAO gameDAO) {
            this.gameDAO = gameDAO;
        }

        @Override
        protected Void doInBackground(List<Game>... lists) {
            gameDAO.insertMany(lists[0]);
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

    /**
     * Refresh the games list from the remote database and update the state of the SwipeRefreshLayout
     *
     * @param swipeRefreshLayout : is passed to the method so that the refreshing animation can be
     *                           canceled once the data is retrieved.
     */
    public void refreshData(final SwipeRefreshLayout swipeRefreshLayout) {

        Log.d("gameApp", "game reviewer : refreshData() called");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                webService.getGames().enqueue(new Callback<List<Game>>() {
                    @Override
                    public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                        Log.d("GameRepository", "onResponse: GOT CALL FROM REMOTE DB");
                        List<Game> games = response.body();
                        assert games != null;
                        swipeRefreshLayout.setRefreshing(false);
                        new InsertAllGameAsyncTask(gameDAO).execute(games);
                    }

                    @Override
                    public void onFailure(Call<List<Game>> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        Log.d("gameApp", "FAILURE IN DB CALL");
                        t.printStackTrace();
                    }
                });
            }
        });
    }
}
