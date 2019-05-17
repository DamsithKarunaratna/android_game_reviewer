package com.ctse.androidgamereviewer.data.dao;

import com.ctse.androidgamereviewer.data.entities.Review;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class ReviewDAO {

    @Insert(onConflict = REPLACE)
    public abstract void insertReview(Review review);

    @Insert(onConflict = REPLACE)
    public abstract void insertMany(List<Review> reviews);

    @Update
    public abstract void update(Review review);

    @Delete
    public abstract void delete(Review review);

    @Query("SELECT * FROM Review WHERE gameId =:gameId")
    public abstract LiveData<List<Review>> getReviewsByGameId(String gameId);

    @Query("SELECT * FROM Review")
    public abstract LiveData<List<Review>> getAllReviews();

    @Query("SELECT * FROM Review WHERE remote_id =:reviewId")
    public abstract LiveData<Review> getReviewById(String reviewId);

}
