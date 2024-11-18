package com.skt.help.gpt.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OpenAIApi {
    @Headers({
            "Authorization: Bearer ", // API 키 설정
            "Content-Type: application/json"
    })
    @POST("v1/chat/completions")
    Call<ResponseBody> sendMessageRaw(@Body RequestBody body);
}
