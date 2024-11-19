package com.skt.help.api;

import com.skt.help.model.TelegramMessage;
import com.skt.help.model.TelegramResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TelegramApi {
//    @POST("bot{token}/sendMessage")
@POST("sendMessage")
    Call<TelegramResponse> sendMessage(@Body TelegramMessage telegramMessage);
}
