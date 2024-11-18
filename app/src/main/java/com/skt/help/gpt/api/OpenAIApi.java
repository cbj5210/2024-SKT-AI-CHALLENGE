package com.skt.help.gpt.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OpenAIApi {
    @Headers({
            "Authorization: Bearer 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", // API 키 설정
            "Content-Type: application/json"
    })
    @POST("v1/chat/completions")
    Call<ResponseBody> sendMessageRaw(@Body RequestBody body);
}
