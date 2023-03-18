package com.example.yad2application.Model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface CurrencyConversionAPI {
    @GET("v1/convertcurrency")
    Call<CurrencyConversion> convertCurrency(
            @Header("X-Api-Key") String apiKey,
            @Query("have") String wantCurrency,
            @Query("want") String haveCurrency,
            @Query("amount") double amount
    );
}
