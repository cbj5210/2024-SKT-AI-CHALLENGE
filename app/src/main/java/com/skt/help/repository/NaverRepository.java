package com.skt.help.repository;

import android.util.Log;

import com.skt.help.api.NaverApi;
import com.skt.help.model.ReverseGeocodeResponse;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Callback;
import retrofit2.Response;

public class NaverRepository {

    private final String clientId = "";
    private final String clientSecret = "";

    private static final String TAG = "MainActivity";
    private final NaverApi naverApi;

    public NaverRepository() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://naveropenapi.apigw.ntruss.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        naverApi = retrofit.create(NaverApi.class);
    }

    public void convert2Address(double latitude, double longitude) {

        Call<ReverseGeocodeResponse> call = naverApi.convert2Address(clientId, clientSecret,
                    longitude + "," + latitude, "json", "legalcode,admcode,addr,roadaddr");

        call.enqueue(new Callback<ReverseGeocodeResponse>() {
            @Override
            public void onResponse(Call<ReverseGeocodeResponse> call, Response<ReverseGeocodeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReverseGeocodeResponse result = response.body();
                    for (ReverseGeocodeResponse.Result res : result.getResults()) {
                        Log.d(TAG, "주소: " + res.getRegion().getArea1().getName() + ", "
                                + res.getRegion().getArea2().getName() + ", "
                                + res.getRegion().getArea3().getName());
                    }
                } else {
                    Log.e(TAG, "API 요청 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ReverseGeocodeResponse> call, Throwable t) {
                Log.e(TAG, "API 호출 실패", t);
            }
        });


    }
}
