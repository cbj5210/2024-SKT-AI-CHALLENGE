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

    public interface ReverseGeocodeCallback {
        void onSuccess(String address);  // 성공 시 주소 반환
        void onError(String errorMessage);  // 실패 시 에러 메시지 반환
    }

    public void convert2Address(String coodrnidate, ReverseGeocodeCallback callback) {

        Call<ReverseGeocodeResponse> call = naverApi.convert2Address(clientId, clientSecret,
                coodrnidate, "json", "roadaddr");

        call.enqueue(new Callback<ReverseGeocodeResponse>() {
            @Override
            public void onResponse(Call<ReverseGeocodeResponse> call, Response<ReverseGeocodeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReverseGeocodeResponse result = response.body();
                    StringBuilder addressBuilder = new StringBuilder();
                    for (ReverseGeocodeResponse.Result res : result.getResults()) {
                        addressBuilder.append(res.getRegion().getArea1().getName()).append(" ")
                                .append(res.getRegion().getArea2().getName()).append(" ")
                                .append(res.getRegion().getArea3().getName()).append(" ")
                                .append(res.getLand().getName()).append(" ")
                                .append(res.getLand().getNumber1());
                        /*if(res.getLand().getNumber2().isEmpty()) {
                            addressBuilder.append("\n");
                        } else {
                            addressBuilder.append("-").append(res.getLand().getNumber2()).append("\n");
                        }*/
                    }
                    String address = addressBuilder.toString();
                    Log.d(TAG, "주소: " + address);
                    callback.onSuccess(address);

                } else {
                    Log.e(TAG, "API 요청 실패: " + response.message());
                    callback.onError("API 요청 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ReverseGeocodeResponse> call, Throwable t) {
                Log.e(TAG, "API 호출 실패", t);
            }
        });


    }
}
