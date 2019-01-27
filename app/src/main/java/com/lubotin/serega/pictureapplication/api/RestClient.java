package com.lubotin.serega.pictureapplication.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static final String IMGUR_BASE_URL = "https://api.imgur.com/";

    private static final RestClient instance = new RestClient();

    public static Api getImgurInstance() {
        return instance.imgurService;
    }

    private final Api imgurService;

    //Private constructor for retrofit build
    private RestClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IMGUR_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        imgurService = retrofit.create(Api.class);

    }

}
