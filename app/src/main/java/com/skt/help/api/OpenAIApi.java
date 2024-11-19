package com.skt.help.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OpenAIApi {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    Call<ResponseBody> sendMessageRaw(@Header("Authorization") String authorization, @Body RequestBody body);
}
