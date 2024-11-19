package com.skt.help.api;

import com.skt.help.model.ReverseGeocodeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface NaverApi {

    @GET("map-reversegeocode/v2/gc")
    Call<ReverseGeocodeResponse> convert2Address(@Header("x-ncp-apigw-api-key-id") String apiKeyId,
                                                 @Header("x-ncp-apigw-api-key") String apiKey,
                                                 @Query("coords") String coords,  // 위도, 경도 좌표
                                                 @Query("output") String output,  // 응답 형식 (json 또는 xml)
                                                 @Query("orders") String orders   // 반환할 주소 유형 (legalcode, admcode, addr, roadaddr 등)
     );
}
