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

public class GameRepository {

    private static int REFRESH_TIMEOUT_IN_MINUTES = 3;

    private GameDAO gameDAO;
    private GameWebService webService;
    private Executor executor;
    private GameDatabase database;

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
        allGames = gameDAO.getAllGames();

//        refreshData();
    }

    public void insert(final Game game) {
        // Insert game into local SQLite Database
        new InsertGameAsyncTask(gameDAO).execute(game);

        // Thread to insert game into remote MongoDB database
        executor.execute(new Runnable() {
            @Override
            public void run() {
                webService.saveGame(game).enqueue(new Callback<Game>() {
                    @Override
                    public void onResponse(Call<Game> call, Response<Game> response) {
                        System.out.println("Game saved to online DB");
//                        System.out.println(response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<Game> call, Throwable t) {
                        System.out.println("Game not saved");
                        t.printStackTrace();
                    }
                });
            }
        });
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

    public void refreshData(final SwipeRefreshLayout swipeRefreshLayout) {

        System.out.println("refreshData() called");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                webService.getGames().enqueue(new Callback<List<Game>>() {
                    @Override
                    public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                        System.out.println("------ GOT CALL FROM REMOTE DB -------");
                        Log.d("GameRepository", "onResponse: GOT CALL FROM REMOTE DB");
                        List<Game> games = response.body();
                        assert games != null;
                        for (Game g : games) {
                            System.out.println(g.getId());
                            System.out.println(g.getGenre());
                            System.out.println(g.getTitle());
                            System.out.println(g.getRelease_date());
                            System.out.println(g.getImage());
                            System.out.println(g.get_id());
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        new InsertAllGameAsyncTask(gameDAO).execute(games);
                    }

                    @Override
                    public void onFailure(Call<List<Game>> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        System.out.println("FAILURE IN DB CALL");
                        t.printStackTrace();
                    }
                });
            }
        });
    }
}
