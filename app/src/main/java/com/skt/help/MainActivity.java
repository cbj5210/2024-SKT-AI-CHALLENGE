package com.skt.help;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;

import com.skt.help.service.gpt.GptService;
import com.skt.help.service.location.AddressService;
import com.skt.help.service.location.LocationService;
import com.skt.help.service.sns.SnsService;

public class MainActivity extends AppCompatActivity {
    private GptService gptService;
    private SnsService service;

    private LocationService locationService;
    private AddressService addressService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 필요한 권한 요청
        requestPermission();

        // 텍스트 입력창 정의
        TextInputEditText messageInput = findViewById(R.id.custom_message_text);
        TextInputEditText numberInput = findViewById(R.id.custom_number_text);

        // 버튼 정의
        Button testStartButton = findViewById(R.id.testStartButton);
        Button testStopButton = findViewById(R.id.testStopButton);
        Button serviceStartButton = findViewById(R.id.serviceStartButton);
        Button serviceStopButton = findViewById(R.id.serviceStopButton);

        // 테스트 시작 버튼 클릭
        testStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //버튼 비활성화 및 색상 변경
                messageInput.setEnabled(false);
                numberInput.setEnabled(false);
                testStartButton.setEnabled(false);
                testStartButton.setBackgroundResource(R.drawable.rounded_grey_button);
                serviceStartButton.setEnabled(false);
                serviceStartButton.setBackgroundResource(R.drawable.rounded_grey_button);
                serviceStopButton.setEnabled(false);
                serviceStopButton.setBackgroundResource(R.drawable.rounded_grey_button);

                // Foreground Service 실행
                Toast.makeText(MainActivity.this, "테스트 시작", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Foreground.class);
                intent.putExtra("isReal", false);
                startService(intent);
            }
        });

        // 테스트 종료 버튼 클릭
        testStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageInput.setEnabled(true);
                numberInput.setEnabled(true);
                testStartButton.setEnabled(true);
                testStartButton.setBackgroundResource(R.drawable.rounded_real_green_button);
                serviceStartButton.setEnabled(true);
                serviceStartButton.setBackgroundResource(R.drawable.rounded_real_red_button);
                serviceStopButton.setEnabled(true);
                serviceStopButton.setBackgroundResource(R.drawable.rounded_red_button);

                Toast.makeText(MainActivity.this, "테스트 종료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Foreground.class);
                stopService(intent);
            }
        });

        // 서비스 시작 버튼 클릭
        serviceStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageInput.setEnabled(false);
                numberInput.setEnabled(false);
                testStartButton.setEnabled(false);
                testStartButton.setBackgroundResource(R.drawable.rounded_grey_button);
                testStopButton.setEnabled(false);
                testStopButton.setBackgroundResource(R.drawable.rounded_grey_button);
                serviceStartButton.setEnabled(false);
                serviceStartButton.setBackgroundResource(R.drawable.rounded_grey_button);

                Toast.makeText(MainActivity.this, "위험 감지 시작", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Foreground.class);
                intent.putExtra("isReal", true);
                startService(intent);
            }
        });

        // 서비스 종료 버튼 클릭
        serviceStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageInput.setEnabled(true);
                numberInput.setEnabled(true);
                testStartButton.setEnabled(true);
                testStartButton.setBackgroundResource(R.drawable.rounded_real_green_button);
                testStopButton.setEnabled(true);
                testStopButton.setBackgroundResource(R.drawable.rounded_green_button);
                serviceStartButton.setEnabled(true);
                serviceStartButton.setBackgroundResource(R.drawable.rounded_real_red_button);

                Toast.makeText(MainActivity.this, "위험 감지 종료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Foreground.class);
                stopService(intent);
            }
        });


        // 주상님 버튼
        Button btn_location = findViewById(R.id.button5);
        locationService = new LocationService(this);
        addressService = new AddressService();
        btn_location.setOnClickListener(view -> {
            locationService.getLastKnownLocation(new LocationService.LocationCallbackListener() {
                @Override
                public void onLocationReceived(double latitude, double longitude) {
                    double myLatitude = 37.339578;
                    double myLongitude = 127.092850;
                    String locationText = "위도: " + latitude + "\n경도: " + longitude;
                    Toast.makeText(MainActivity.this, locationText, Toast.LENGTH_SHORT).show();
                    addressService.convert(myLatitude, myLongitude);
                }

                @Override
                public void onLocationError(String errorMsg) {
                    Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // 하혁님 버튼
        Button btn_gpt = findViewById(R.id.button6);
        gptService = new GptService(this);
        service = new SnsService();
        btn_gpt.setOnClickListener(view -> {
            new Thread(() -> {
                try {
                    String speech = "왜 이러세요 살려주세요. 누가좀 도와주세요";
                    String gptResponse = gptService.process(speech);
                    service.sendTelegramMessage("위험 수치 : " + gptResponse);
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, gptResponse, Toast.LENGTH_SHORT).show();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });

        // 권한 확인 및 요청 코드 추가 (MainActivity의 onCreate()에서)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);  // REQUEST_CODE는 원하는 값으로 설정 가능
        }

    }

    private void requestPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(
                    this,
                    new String[] {android.Manifest.permission.RECORD_AUDIO}, 0
            );
        }

        // todo
        // gps, sms, notification
    }
}