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

public class ReviewRepository {

    private ReviewDAO reviewDAO;
    private GameWebService webService;
    private Executor executor;
    private GameDatabase database;

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

    public void insert(final Review review) {
        new InsertReviewAsyncTask(reviewDAO).execute(review);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                webService.saveReview(review).enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(Call<Review> call, Response<Review> response) {
                        System.out.println("Game saved to online DB");
                        System.out.println(response.body().toString());
                        Log.d("ReviewRepository", "onResponse: Game saved to online DB");
                    }

                    @Override
                    public void onFailure(Call<Review> call, Throwable t) {
                        System.out.println("Game not saved");
                        Log.d("ReviewRepository", "onResponse: Game not saved");
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    public void update(Review review) {
        new UpdateReviewAsyncTask(reviewDAO).execute(review);
    }

    public void delete(Review review) {
        new DeleteReviewAsyncTask(reviewDAO).execute(review);
    }

    public LiveData<List<Review>> getAllReviews() {
        return allReviews;
    }

    public LiveData<List<Review>> getReviewsForGame(String game_id) {
        return database.reviewDAO().getReviewsByGameId(game_id);
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

    public void refreshReviews() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                webService.getReviews().enqueue(new Callback<List<Review>>() {
                    @Override
                    public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                        System.out.println("------ GOT CALL FROM REMOTE DB -------");
                        Log.d("ReviewRepository", "onResponse: GOT CALL FROM REMOTE DB");
                        List<Review> reviews = response.body();
                        assert response != null;
                        for (Review r : reviews) {
                            System.out.println(r.get_id());
                            System.out.println(r.getBody());
                            System.out.println(r.getDate());
                            System.out.println(r.getGameId());
                            System.out.println(r.getId());
                            System.out.println(r.getRating());
                            System.out.println(r.getTitle());
                            System.out.println(r.getUserEmail());
                        }

                        new InsertAllReviewsAsyncTask(reviewDAO).execute(reviews);
                    }

                    @Override
                    public void onFailure(Call<List<Review>> call, Throwable t) {
                        System.out.println("FAILURE IN DB CALL");
                        Log.d("ReviewRepository", "onResponse: FAILURE IN DB CALL");
                        t.printStackTrace();
                    }
                });
            }
        });
    }

}
