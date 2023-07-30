package com.example.myapplication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiServerci {
    Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();

    ApiServerci apiServerci = new Retrofit.Builder()
            .baseUrl("http://103.118.28.46:3000/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiServerci.class);

    @GET("get-list-quote")
    Call<List<String>> getListQuote(@Query("num") int num);
}
