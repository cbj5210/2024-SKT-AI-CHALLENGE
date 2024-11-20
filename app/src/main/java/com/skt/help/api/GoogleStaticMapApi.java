package com.skt.help.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleStaticMapApi {
    @GET("maps/api/staticmap")
    Call<ResponseBody> getStaticMap(@Query("size") String size, @Query("path") String path, @Query("key") String apiKey);
}
