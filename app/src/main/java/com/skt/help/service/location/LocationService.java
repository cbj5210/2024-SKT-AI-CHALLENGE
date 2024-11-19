package com.skt.help.service.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationService {
    private final FusedLocationProviderClient fusedLocationClient;
    private final Context context;

    // 콜백 인터페이스 정의
    public interface LocationCallbackListener {
        void onLocationReceived(double latitude, double longitude);
        void onLocationError(String errorMsg);
    }

    public LocationService(Context context) {
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    // 마지막으로 알려진 위치 가져오기
    public void getLastKnownLocation(LocationCallbackListener callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            callback.onLocationError("위치 권한이 없습니다.");
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                callback.onLocationReceived(latitude, longitude);
            } else {
                callback.onLocationError("위치를 가져올 수 없습니다.");
            }
        });
    }

    // 실시간 위치 업데이트 요청
    public void requestLocationUpdates(LocationCallbackListener callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            callback.onLocationError("위치 권한이 없습니다.");
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                                                            .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        callback.onLocationReceived(latitude, longitude);
                    }
                } else {
                    callback.onLocationError("실시간 위치 업데이트 실패");
                }
            }
        }, null);
    }

}
