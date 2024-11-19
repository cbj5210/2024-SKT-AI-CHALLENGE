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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.skt.help.service.gpt.GptService;
import com.skt.help.service.sns.SnsService;

public class MainActivity extends AppCompatActivity {
    private GptService gptService;
    private SnsService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 필요한 권한 요청
        requestPermission();

        // 버튼 정의
        Button testStartButton = findViewById(R.id.testStartButton);
        Button testStopButton = findViewById(R.id.testStopButton);
        Button serviceStartButton = findViewById(R.id.serviceStartButton);
        Button serviceStopButton = findViewById(R.id.serviceStopButton);

        // 테스트 시작 버튼 클릭
        testStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo : 나머지 버튼 비활성화 및 색상 변경

                Toast.makeText(MainActivity.this, "테스트 시작", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Foreground.class);
                startService(intent);
            }
        });

        // 테스트 종료 버튼 클릭
        testStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "테스트 종료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Foreground.class);
                stopService(intent);
            }
        });

        // 서비스 시작 버튼 클릭
        serviceStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "위험 감지 시작", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Foreground.class);
                startService(intent);
            }
        });

        // 서비스 종료 버튼 클릭
        serviceStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "위험 감지 종료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Foreground.class);
                stopService(intent);
            }
        });


        // 주상님 버튼
        Button btn_location = findViewById(R.id.button5);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "현재 주소는 : 을지로 2가", Toast.LENGTH_SHORT).show();
            }
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
        // gps, sms
    }
}