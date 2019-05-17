/**
 * CTSE Android Project - Game Reviewer
 * @author IT16037434 Karunaratne D. C.
 * @author IT15146366 Hettiarachchi H. A. I. S.
 *
 * File: ReviewViewModel.java
 */
package com.ctse.androidgamereviewer;

import android.app.Application;

import com.ctse.androidgamereviewer.data.ReviewRepository;
import com.ctse.androidgamereviewer.data.entities.Review;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * Abstracts Data handling logic out of the MainActivity UI controller.
 * Consists of a gameRepository which handles CRUD operations at a high level.
 * <p>
 * See <a href="https://developer.android.com/topic/libraries/architecture/viewmodel">
 * Documentation </a> for more details.
 **/
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

    public LiveData<List<Review>> getReviewForGame(String game_id) {
        return reviewRepository.getReviewsForGame(game_id);
    }

    public LiveData<Review> getReviewById(String review_id) {
        return reviewRepository.getReviewById(review_id);
    }

    public ReviewRepository getReviewRepository() {
        return reviewRepository;
    }
}
