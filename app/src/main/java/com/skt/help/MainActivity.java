package com.skt.help;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.skt.help.data.database.DatabaseHelper;
import com.skt.help.repository.DatabaseRepository;
import com.skt.help.model.UserCondition;
import com.skt.help.service.gpt.GptService;
import com.skt.help.service.location.AddressService;
import com.skt.help.service.location.LocationService;
import com.skt.help.service.mlmodel.EmbeddedModelService;
import com.skt.help.service.sns.SnsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MainActivity extends AppCompatActivity {
    private GptService gptService;
    private SnsService service;

    private LocationService locationService;
    private AddressService addressService;
    private EmbeddedModelService embeddedModelService;

    private DatabaseRepository databaseRepository;
    private final long id = 1;
    private final Handler handler = new Handler();
    private Runnable saveRunnable;

    // 버튼 정의
    MaterialButton testStartButton;
    MaterialButton testStopButton;
    MaterialButton serviceStartButton;
    MaterialButton serviceStopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 필요한 권한 요청
        requestPermission();

        // 텍스트 입력창 정의
        EditText messageInput = findViewById(R.id.custom_emergency_message_text);
        EditText customStatusInput = findViewById(R.id.custom_status_text);

        // 설정 저장
        databaseRepository = new DatabaseRepository(this);
        databaseRepository.open();

        UserCondition userCondition = null;
        if(databaseRepository.isTableEmpty(DatabaseHelper.TABLE_NAME)) {
            // empty 이기 때문에 기초값 insert 하고 객체 리턴
            userCondition = databaseRepository.insertInitialUserCondition();

        } else {
            userCondition = databaseRepository.fetchUserCondition(id);
        }

        if(!Objects.isNull(userCondition)) {
            messageInput.setText(userCondition.keyword());
            customStatusInput.setText(userCondition.conditions());
        }

        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (saveRunnable != null) {
                    handler.removeCallbacks(saveRunnable);
                }
                saveRunnable = () -> databaseRepository.updateUser(id, s.toString(), customStatusInput.getText().toString());  // 사용자 ID 1로 가정
                handler.postDelayed(saveRunnable, 2000);  // 2초 후에 DB 업데이트 실행

            }
        });
        customStatusInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (saveRunnable != null) {
                    handler.removeCallbacks(saveRunnable);
                }
                saveRunnable = () -> databaseRepository.updateUser(id, messageInput.getText().toString(), s.toString());  // 사용자 ID 1로 가정
                handler.postDelayed(saveRunnable, 2000);  // 2초 후에 DB 업데이트 실행
            }
        });

        // 버튼 정의
        testStartButton = findViewById(R.id.testStartButton);
        testStopButton = findViewById(R.id.testStopButton);
        serviceStartButton = findViewById(R.id.serviceStartButton);
        serviceStopButton = findViewById(R.id.serviceStopButton);

        // 테스트 시작 버튼 클릭
        testStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //버튼 비활성화 및 색상 변경
                messageInput.setEnabled(false);
                customStatusInput.setEnabled(false);
                buttonDisable(testStartButton);
                testStopButtonEnable();
                buttonDisable(serviceStartButton);
                buttonDisable(serviceStopButton);

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
                customStatusInput.setEnabled(true);
                testStartButtonEnable();
                buttonDisable(testStopButton);
                serviceStartButtonEnable();
                buttonDisable(serviceStopButton);

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
                customStatusInput.setEnabled(false);
                buttonDisable(testStartButton);
                buttonDisable(testStopButton);
                buttonDisable(serviceStartButton);
                serviceStopButtonEnable();


                Toast.makeText(MainActivity.this, "위험 감지 시작", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Foreground.class);
                intent.putExtra("isReal", true);
                intent.putExtra("messageInput", Objects.requireNonNull(messageInput.getText()).toString().replaceAll(" ", ""));
                intent.putExtra("customStatusInput", Optional.ofNullable(customStatusInput.getText()).map(Object::toString).orElse(null));
                startService(intent);
            }
        });

        // 서비스 종료 버튼 클릭
        serviceStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageInput.setEnabled(true);
                customStatusInput.setEnabled(true);
                testStartButtonEnable();
                buttonDisable(testStopButton);
                serviceStartButtonEnable();
                buttonDisable(serviceStopButton);

                Toast.makeText(MainActivity.this, "위험 감지 종료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Foreground.class);
                stopService(intent);
            }
        });
    }

    private void requestPermission() {
        List<String> permissions = new ArrayList<>();

        // RECORD_AUDIO 권한 확인
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.RECORD_AUDIO);
        }

        // GPS 권한 확인
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        // SMS 권한 확인
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.SEND_SMS);
        }

        // Notification 권한 확인 (안드로이드 13 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.POST_NOTIFICATIONS);
        }

        // 요청할 권한이 있다면 한 번에 요청
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissions.toArray(new String[0]),
                    0 // requestCode는 하나로 통합
            );
        }
    }

    private void testStartButtonEnable() {
        testStartButton.setEnabled(true);
        testStartButton.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        testStartButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.testButtonColor)));
        testStartButton.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        testStartButton.setStrokeWidth(2);
    }

    private void testStopButtonEnable() {
        testStopButton.setEnabled(true);
        testStopButton.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.testButtonColor)));
        testStopButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        testStopButton.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.testButtonColor)));
        testStopButton.setStrokeWidth(2);
    }

    private void serviceStartButtonEnable() {
        serviceStartButton.setEnabled(true);
        serviceStartButton.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        serviceStartButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.serviceButtonColor)));
        serviceStartButton.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        serviceStartButton.setStrokeWidth(2);
    }

    private void serviceStopButtonEnable() {
        serviceStopButton.setEnabled(true);
        serviceStopButton.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.serviceButtonColor)));
        serviceStopButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        serviceStopButton.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.serviceButtonColor)));
        serviceStopButton.setStrokeWidth(2);
    }

    private void buttonDisable(MaterialButton button) {
        button.setEnabled(false);
        button.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.disabledButtonText)));
        button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.disabledButtonBackground)));
        button.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        button.setStrokeWidth(2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseRepository.close();  // 액티비티 종료 시 DB 닫기
    }
}