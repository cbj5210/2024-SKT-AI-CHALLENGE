package com.skt.help.repository;

import com.skt.help.api.GoogleStaticMapApi;
import com.skt.help.api.TelegramApi;
import com.skt.help.model.TelegramMessage;
import com.skt.help.model.TelegramResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleStaticMapRepository {
    private static final String GOOGLE_MAP_KEY = "";

    private final GoogleStaticMapApi googleStaticMapApi;

    public GoogleStaticMapRepository() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        googleStaticMapApi = retrofit.create(GoogleStaticMapApi.class);
    }


}
