package com.skt.help.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.skt.help.api.TelegramApi;
import com.skt.help.model.TelegramMessage;
import com.skt.help.model.TelegramResponse;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TelegramRepository {
    private static final String BOT_TOKEN = "7916140275:AAHEa2Q-fgSLGAXF_PRvoRXUj_hZI9RwtCE";
    private static final String CHAT_ID = "56126416";

    private final TelegramApi telegramApi;

    public TelegramRepository() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.telegram.org/bot7916140275:AAHEa2Q-fgSLGAXF_PRvoRXUj_hZI9RwtCE/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        telegramApi = retrofit.create(TelegramApi.class);
    }

    public void sendMessage(String message) {
        try {
            Call<TelegramResponse> call = telegramApi.sendMessage(new TelegramMessage(CHAT_ID, message));
            System.out.println("log:: " + call.request().url().toString());

            Response<TelegramResponse> response = call.execute();

            if (!response.isSuccessful()) {
                throw new RuntimeException("API Error: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch response from API", e);
        }

    }
}
