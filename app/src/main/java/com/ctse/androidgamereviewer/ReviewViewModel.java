package com.ctse.androidgamereviewer;

import android.app.Application;

import com.ctse.androidgamereviewer.data.ReviewRepository;
import com.ctse.androidgamereviewer.data.entities.Review;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ReviewViewModel extends AndroidViewModel {

    private ReviewRepository reviewRepository;
    private LiveData<List<Review>> allReviews;

    public ReviewViewModel(@NonNull Application application) {
        super(application);
        reviewRepository = new ReviewRepository(application);
        allReviews = reviewRepository.getAllReviews();
    }

    public void insert(Review review) {
        reviewRepository.insert(review);
    }

    public void update(Review review) {
        reviewRepository.update(review);
    }

    public void delete(Review review) {
        reviewRepository.delete(review);
    }

    public LiveData<List<Review>> getAllReviews() {
        return allReviews;
    }

    public ReviewRepository getReviewRepository() {
        return reviewRepository;
    }
}
