package com.ctse.androidgamereviewer.data;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.ctse.androidgamereviewer.data.dao.ReviewDAO;
import com.ctse.androidgamereviewer.data.entities.Review;
import com.ctse.androidgamereviewer.data.retrofit.GameWebService;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
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
public class ReviewRepository {

    /**
     * ReviewDAO is Room Data access object for handling local DB operations
     *
     * @see androidx.room.Dao
     */
    private ReviewDAO reviewDAO;
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
    private LiveData<List<Review>> allReviews;

    public ReviewRepository(Application application) {
        database = GameDatabase.getInstance(application);
        reviewDAO = database.reviewDAO();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ctse-test-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webService = retrofit.create(GameWebService.class);
        executor = Executors.newSingleThreadExecutor();

        allReviews = reviewDAO.getAllReviews();
    }

    /**
     * inserts a new review object into the database. Game is inserted into the local database as
     * well as the remote database. To avoid blocking the main thread, database operations are
     * carried out on separate threads. This enables a smooth user experience.
     *
     * @param review review object to be persisted.
     */
    public void insert(final Review review) {
        new InsertReviewAsyncTask(reviewDAO).execute(review);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                webService.saveReview(review).enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(Call<Review> call, Response<Review> response) {
                        Log.d("ReviewRepository", "onResponse: Game saved to online DB");
                    }

                    @Override
                    public void onFailure(Call<Review> call, Throwable t) {
                        Log.d("ReviewRepository", "onResponse: Game not saved");
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    public void update(final Review review) {
        new UpdateReviewAsyncTask(reviewDAO).execute(review);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                webService.updateReview(review.get_id(), review).enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(Call<Review> call, Response<Review> response) {
                        System.out.println("Review edited on online DB");
                        System.out.println(response.body().toString());
                        Log.d("ReviewRepository", "onResponse: Review edited on online DB");
                    }

                    @Override
                    public void onFailure(Call<Review> call, Throwable t) {
                        System.out.println("Game not edited");
                        Log.d("ReviewRepository", "onResponse: Game not edited");
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    public void delete(final Review review) {
        new DeleteReviewAsyncTask(reviewDAO).execute(review);

        executor.execute((new Runnable() {
            @Override
            public void run() {
                webService.deleteReview(review.get_id()).enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(Call<Review> call, Response<Review> response) {
                        System.out.println("Review deleted online DB");
                        System.out.println(response.body().toString());
                        Log.d("ReviewRepository", "onResponse: Review deleted online DB");
                    }

                    @Override
                    public void onFailure(Call<Review> call, Throwable t) {
                        System.out.println("Review not deleted");
                        Log.d("ReviewRepository", "onResponse: Review not deleted");
                        t.printStackTrace();
                    }
                });
            }
        }));
    }

    public LiveData<List<Review>> getAllReviews() {
        return allReviews;
    }

    public LiveData<List<Review>> getReviewsForGame(String game_id) {
        return database.reviewDAO().getReviewsByGameId(game_id);
    }

    public LiveData<Review> getReviewById(String review_id) {
        return database.reviewDAO().getReviewById(review_id);
    }

    private static class InsertReviewAsyncTask extends AsyncTask<Review, Void, Void> {

        private ReviewDAO reviewDAO;

        private InsertReviewAsyncTask(ReviewDAO reviewDAO) {
            this.reviewDAO = reviewDAO;
        }

        @Override
        protected Void doInBackground(Review... reviews) {
            reviewDAO.insertReview(reviews[0]);
            return null;
        }
    }

    private static class InsertAllReviewsAsyncTask extends AsyncTask<List<Review>, Void, Void> {

        private ReviewDAO reviewDAO;

        public InsertAllReviewsAsyncTask(ReviewDAO reviewDAO) {
            this.reviewDAO = reviewDAO;
        }

        @Override
        protected Void doInBackground(List<Review>... lists) {
            reviewDAO.insertMany(lists[0]);
            return null;
        }
    }

    private static class UpdateReviewAsyncTask extends AsyncTask<Review, Void, Void> {

        private ReviewDAO reviewDAO;

        private UpdateReviewAsyncTask(ReviewDAO reviewDAO) {
            this.reviewDAO = reviewDAO;
        }

        @Override
        protected Void doInBackground(Review... reviews) {
            reviewDAO.update(reviews[0]);
            return null;
        }
    }

    private static class DeleteReviewAsyncTask extends AsyncTask<Review, Void, Void> {

        private ReviewDAO reviewDAO;

        private DeleteReviewAsyncTask(ReviewDAO reviewDAO) {
            this.reviewDAO = reviewDAO;
        }

        @Override
        protected Void doInBackground(Review... reviews) {
            reviewDAO.delete(reviews[0]);
            return null;
        }
    }

    /**
     * Refresh the list of reviews from the remote database.
     */
    public void refreshReviews() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                webService.getReviews().enqueue(new Callback<List<Review>>() {
                    @Override
                    public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                        Log.d("ReviewRepository", "onResponse: GOT CALL FROM REMOTE DB");
                        List<Review> reviews = response.body();
                        assert response != null;
                        new InsertAllReviewsAsyncTask(reviewDAO).execute(reviews);
                    }

                    @Override
                    public void onFailure(Call<List<Review>> call, Throwable t) {
                        Log.d("ReviewRepository", "onResponse: FAILURE IN DB CALL");
                        t.printStackTrace();
                    }
                });
            }
        });
    }

}
