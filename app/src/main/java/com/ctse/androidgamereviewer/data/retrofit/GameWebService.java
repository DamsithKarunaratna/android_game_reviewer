package com.ctse.androidgamereviewer.data.retrofit;

import com.ctse.androidgamereviewer.data.entities.Game;
import com.ctse.androidgamereviewer.data.entities.Review;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GameWebService {

    @GET("games/")
    Call<List<Game>> getGames();

    @POST("/games")
    Call<Game> saveGame(@Body Game game);

    @POST("/reviews")
    Call<Review> saveReview(@Body Review review);

    @PUT("/reviews/{review_id}")
    Call<Review> updateReview(@Path("review_id") String review_id, @Body Review review);

    @DELETE("/reviews/{review_id}")
    Call<Review> deleteReview(@Path("review_id") String review_id);

    @GET("reviews/")
    Call<List<Review>> getReviews();
}
