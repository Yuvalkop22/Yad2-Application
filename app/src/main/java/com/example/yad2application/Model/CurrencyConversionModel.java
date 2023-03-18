package com.example.yad2application.Model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrencyConversionModel {
        final public static CurrencyConversionModel instance = new CurrencyConversionModel();

        final String BASE_URL = "https://api.api-ninjas.com/";
        Retrofit retrofit;
        CurrencyConversionAPI currencyConversionAPI;
        private CurrencyConversionModel(){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            currencyConversionAPI = retrofit.create(CurrencyConversionAPI.class);

        }

        public LiveData<CurrencyConversion> convertCurrency(String wantCurrency, String haveCurrency, double amount){
            MutableLiveData<CurrencyConversion> data = new MutableLiveData<>();
            Call<CurrencyConversion> call = currencyConversionAPI.convertCurrency("vypyTrsP5zqQlGRYJvgtIA==5OrTsL3DOyaj7v6Z", wantCurrency, haveCurrency, amount);
            call.enqueue(new Callback<CurrencyConversion>() {
                @Override
                public void onResponse(Call<CurrencyConversion> call, Response<CurrencyConversion> response) {
                    if (response.isSuccessful()) {
                        CurrencyConversion res = response.body();
                        data.setValue(res);
                    } else {
                        Log.d("TAG", "----getJoke call error " + call.toString());
                        Log.d("TAG", "----getJoke response error " + response.code());
                        Log.d("TAG", "----getJoke response error " + response.errorBody().toString());                }
                }

                @Override
                public void onFailure(Call<CurrencyConversion> call, Throwable t) {
                    Log.d("TAG", "----getJoke fail");
                }
            });



            return data;
        }

    }
