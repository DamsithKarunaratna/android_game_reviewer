package com.ctse.androidgamereviewer.data.retrofit;

import com.ctse.androidgamereviewer.data.entities.Game;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GameWebService {

    @GET("games/")
    Call<List<Game>> getGames();
}
