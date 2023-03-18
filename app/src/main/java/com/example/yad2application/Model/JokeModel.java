package com.example.yad2application.Model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JokeModel {
    final public static JokeModel instance = new JokeModel();

    final String BASE_URL = "https://api.api-ninjas.com/";
    Retrofit retrofit;
    JokesApi jokesAPI;
    private JokeModel(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jokesAPI = retrofit.create(JokesApi.class);

    }

    public LiveData<List<Joke>> getJoke(int num){
        MutableLiveData<List<Joke>> data = new MutableLiveData<>();
        Call<List<Joke>> call = jokesAPI.getJokes("vypyTrsP5zqQlGRYJvgtIA==5OrTsL3DOyaj7v6Z",3);
        call.enqueue(new Callback<List<Joke>>() {
            @Override
            public void onResponse(Call<List<Joke>> call, Response<List<Joke>> response) {
                if (response.isSuccessful()) {
                    List<Joke> dadJokes = response.body();
                    data.setValue(dadJokes);
                } else {
                   Log.d("TAG", "----getJoke call error " + call.toString());
                   Log.d("TAG", "----getJoke response error " + response.code());
                   Log.d("TAG", "----getJoke response error " + response.errorBody().toString());                }
            }

            @Override
            public void onFailure(Call<List<Joke>> call, Throwable t) {
                Log.d("TAG", "----getJoke fail");
            }
        });



        return data;
    }

}
