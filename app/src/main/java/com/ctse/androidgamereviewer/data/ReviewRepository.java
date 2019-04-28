package com.ctse.androidgamereviewer.data;

import android.app.Application;
import android.os.AsyncTask;

import com.ctse.androidgamereviewer.data.dao.ReviewDAO;
import com.ctse.androidgamereviewer.data.entities.Review;
import com.ctse.androidgamereviewer.data.retrofit.GameWebService;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.lifecycle.LiveData;

public class ReviewRepository {

    private ReviewDAO reviewDAO;
    private GameWebService gameWebService;
    private Executor executor;

    private LiveData<List<Review>> allReviews;

    public ReviewRepository(Application application) {
        GameDatabase database = GameDatabase.getInstance(application);
        reviewDAO = database.reviewDAO();

        // TODO: get reviews from remote db

        allReviews = reviewDAO.getAllReviews();
    }

    public void insert(final Review review) {
        new InsertReviewAsyncTask(reviewDAO).execute(review);
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


    // TODO: implement insert all reviews task
    // TODO: implement refresh database

}
