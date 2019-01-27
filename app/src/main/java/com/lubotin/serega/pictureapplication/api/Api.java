package com.lubotin.serega.pictureapplication.api;

import com.lubotin.serega.pictureapplication.model.Image;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {
    @Multipart
    //Authorization params using retrofit headers: ID and token
    @Headers({"Authorization: Client-ID 701cc20fc1d9c9b",
            "Access-token: Bearer 445ef0972a7d7f69e74ef37c9ebc60a16e3afecb"})
    @POST("3/image")
    Call<Image> postImage(@Part MultipartBody.Part file);
}