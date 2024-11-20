package com.skt.help;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.util.CollectionUtils;
import com.skt.help.model.ChatGptResponse;
import com.skt.help.service.gpt.GptService;
import com.skt.help.service.sns.SmsService;

import java.util.ArrayList;
import java.util.List;

public class Foreground extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static ObjectMapper objectMapper = new ObjectMapper();

    // isTest? or isReal?
    private boolean isReal;

    // isEmergency?
    private boolean isEmergency = false;

    // inputText & numberText
    private String inputText;
    private String customStatusText;

    // Speech To Text
    private SpeechRecognizer speechRecognizer;
    private Intent intent;
    private List<String> recordMessageList = new ArrayList<>();

    public Foreground() {}

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Foreground onBind() exception");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            isReal = intent.getBooleanExtra("isReal", false);
            inputText = intent.getStringExtra("messageInput");
            customStatusText = intent.getStringExtra("customStatusInput");
        }
        return START_STICKY;
    }

    // ForeGround 액티비티 시작
    @Override
    public void onCreate() {
        super.onCreate();

        // 상태 알림창 추가
        makeNotification();

        // speechRecognizer 셋팅
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        startSpeechRecognizer();
    }

    // ForeGround 액티비티 종료
    @Override
    public void onDestroy() {
        super.onDestroy();

        // 리스너 미종료 및 Memory Leak 방지
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.destroy();
            speechRecognizer = null;
        }

        recordMessageList = null;
    }

    private void makeNotification() {
        String channelId = "default"; // 채널 ID
        String channelName = "기본 채널";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("긴급상황 도와줘")
                .setContentText("긴급상황 탐지 중..")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.RED);

        // 알림 클릭 시 메인 액티비티로 이동
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        // NotificationChannel 설정 (Android 8.0 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH // 중요도를 높임
            );
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        // Foreground Service 시작
        Notification notification = builder.build();
        startForeground(NOTIFICATION_ID, notification);
    }

    private void startSpeechRecognizer(){
        stopSpeechRecognizer();

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(recognitionListener);
        speechRecognizer.startListening(intent);

    }

    private void stopSpeechRecognizer(){
        // 리스너 미종료 및 Memory Leak 방지
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }

    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int i) {
            // todo error message
            startSpeechRecognizer();
        }

        @Override
        public void onResults(Bundle bundle) {
            // 음성 인식 성공
            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String message = matches.get(0);
            String trimMessage = message.replaceAll(" ", "");

            if (isReal) { // 테스트가 아닌 경우
                if (trimMessage.contains(inputText)) { //  위험 감지 메세지가 포함된 경우 새로운 위험이 발생했다고 판단하고
                    recordMessageList = new ArrayList<>(); // list 초기화
                    isEmergency = true;
                }

                recordMessageList.add(message); // list에 음성 텍스트 추가

                // 위험 감지 이후 3번 음성을 인식 하였으면
                if (isEmergency && !CollectionUtils.isEmpty(recordMessageList) && recordMessageList.size() == 3) {

                    String gptRequestMessage = String.join(" ", recordMessageList);

                    // todo : remove for test
                    Toast.makeText(Foreground.this, gptRequestMessage, Toast.LENGTH_SHORT).show();

                    // todo : 현위치 파악

                    // todo : Call GPT
                    GptService gptService = new GptService(getApplicationContext());
                    String gptTextResponse = gptService.process(gptRequestMessage);

                    ChatGptResponse gptResponse;
                    try {
                        gptResponse = objectMapper.readValue(gptTextResponse, ChatGptResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    // 긴급 상황이면
                    if (gptResponse.isEmergency()) {
                        String context = gptResponse.getContext();
                        SmsService smsService = new SmsService();

                        // 단발성 발송일지, 추적 관찰이 필요할지 서비스 분기
                        if (gptResponse.isLocationTracking()) {
                            // todo : 추적 관찰 필요
                            // 위치 정보  새로 받아야함

                        } else {
                            // 단발성 메세지 전송
                            String target = gptResponse.getTarget();
                            if (target != null) {
                                smsService.sendSmsMessage(target, context);
                            }

                            for (String number : gptResponse.getContextTo()) {
                                smsService.sendSmsMessage(number, context);
                            }
                        }
                    }

                    // 긴급 상황 전파 완료
                    isEmergency = false;
                }
            } else { // 테스트인 경우
                Toast.makeText(Foreground.this, message, Toast.LENGTH_SHORT).show();
            }

            startSpeechRecognizer();
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };
}
