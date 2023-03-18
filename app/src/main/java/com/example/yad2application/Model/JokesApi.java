package com.example.yad2application.Model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface JokesApi {
    @GET("v1/dadjokes")
    Call<List<Joke>> getJokes(@Header("X-Api-Key") String apiKey, @Query("limit") int limit);
}
