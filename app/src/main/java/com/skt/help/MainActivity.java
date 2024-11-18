package com.skt.help;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.skt.help.gpt.service.GptService;

public class MainActivity extends AppCompatActivity {
    private GptService gptService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
        btn_gpt.setOnClickListener(view -> {
            // GPT API 호출 및 응답 처리
            new Thread(() -> {
                try {
                    String speech = "왜 이러세요 살려주세요. 누가좀 도와주세요";
                    String response = gptService.process(speech);

                    runOnUiThread(() -> Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });
    }
}