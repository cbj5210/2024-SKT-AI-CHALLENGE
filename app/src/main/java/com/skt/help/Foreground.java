package com.skt.help;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.skt.help.model.ChatGptResponse;
import com.skt.help.repository.NaverRepository;
import com.skt.help.service.gpt.GptService;
import com.skt.help.service.location.AddressService;
import com.skt.help.service.mlmodel.EmbeddedModelService;
import com.skt.help.service.sns.SmsService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Foreground extends Service {
    private static final int NOTIFICATION_ID = 1;
    private final Pattern pattern = Pattern.compile("```json\\s*([\\s\\S]*?)\\s*```");
    private static final ObjectMapper objectMapper = new ObjectMapper();

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

    // Location
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private double currentLatitude; // 위도
    private double currentLongitude; // 경도
    private String currentLocation;

    // sms send
    int currentCount;

    public Foreground() {}

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Foreground onBind() exception");
    }

    // Intent 처리
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

        // 위치 정보
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // GPS 위치 정보 처리
                    //Log.d(TAG, "위도: " + location.getLatitude() + ", 경도: " + location.getLongitude());
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    String coordinate = currentLongitude + "," + currentLatitude;
                    AddressService addressService = new AddressService();
                    addressService.convert(coordinate, new NaverRepository.ReverseGeocodeCallback() {
                        @Override
                        public void onSuccess(String address) {
                            currentLocation = address;
                        }

                        @Override
                        public void onError(String errorMessage) {
                            System.out.println("error");
                        }
                    });
                }
            }
        };

        startLocationUpdates();

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
            Log.d("음성 인식 에러 발생, 에러 코드 :  ", Integer.toString(i));
            startSpeechRecognizer();
        }

        @Override
        public void onResults(Bundle bundle) {
            // 음성 인식 성공
            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String message = matches.get(0);
            String trimMessage = message.replaceAll(" ", "");

            if (isReal) { // 테스트가 아닌 경우
                if (!isEmergency && trimMessage.contains(inputText)) { //  위험 감지 메세지가 포함된 경우 새로운 위험이 발생했다고 판단하고
                    recordMessageList = new ArrayList<>(); // list 초기화
                    isEmergency = true;
                }

                recordMessageList.add(message); // list에 음성 텍스트 추가

                // 위험 감지 이후 3번 음성을 인식 하였으면
                if (isEmergency && !CollectionUtils.isEmpty(recordMessageList) && recordMessageList.size() == 3) {

                    String requestMessage = String.join(" ", recordMessageList);

                    // 모바일 네트워크가 사용 가능하면
                    if (isMobileDataEnabled(getApplicationContext())) {

                        // call GPT
                        GptService gptService = new GptService(getApplicationContext());

                        new Thread(() -> {
                            try {
                                String gptTextResponse = gptService.process(requestMessage, currentLocation, customStatusText);

                                ChatGptResponse gptResponse;
                                try {
                                    Matcher matcher = pattern.matcher(gptTextResponse);
                                    if (matcher.find()) {
                                        gptTextResponse = matcher.group(1);
                                    }
                                    gptResponse = objectMapper.readValue(gptTextResponse, ChatGptResponse.class);
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }

                                // 긴급 상황이면
                                if (gptResponse.getIsEmergency()) {
                                    String context = gptResponse.getContext();
                                    SmsService smsService = new SmsService();

                                    // 단발성 발송일지, 추적 관찰이 필요할지 서비스 분기
                                    int repeatCount = gptResponse.getIsLocationTracking() ? 5 : 1;
                                    currentCount = 0;

                                    Handler handler = new Handler(getMainLooper());
                                    Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            if (currentCount < repeatCount) {
                                                currentCount++;

                                                // 메세지 전송
                                                String target = gptResponse.getTarget();

                                                // todo : 실제로 관공서에 전송되지 않게 임시로 주석 처리
                                                /*if (target != null) {
                                                    smsService.sendSmsMessage(target, context);
                                                }*/

                                                for (String number : gptResponse.getContextTo()) {
                                                    smsService.sendSmsMessage(number, context);
                                                }

                                                handler.postDelayed(this, 60000);
                                            }
                                        }
                                    };

                                    handler.postDelayed(runnable, 0);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();

                    } else {
                        // 데이터 사용이 불가하면 소형 모델로 위험 상황 판단
                        EmbeddedModelService embeddedModelService = new EmbeddedModelService(getApplicationContext());

                        boolean result = embeddedModelService.isEmergency(requestMessage);
                        if (result) {
                            // todo : 녹취 텍스트에 위도 경도를 붙여서 119에 문자 발송
                            // todo : 실제로 관공서에 전송되지 않게 임시로 번호 설정
                            String targetNumber = "010-5353-5210";
                            SmsService smsService = new SmsService();
                            smsService.sendSmsMessage(targetNumber, String.format("위급 상황으로 판단됩니다. 녹취 내용 : %s, 위도 : %s, 경도 : %s", requestMessage, currentLatitude, currentLongitude));
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

    private void startLocationUpdates() {
        LocationRequest locationRequest = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            locationRequest = new LocationRequest.Builder(300000) // 5분 간격
                    .setMinUpdateIntervalMillis(600000) // 최소 업데이트 간격 (10분)
                    .build();
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
        );
    }

    public boolean isMobileDataEnabled(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            // 활성화된 모든 네트워크 가져오기
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities capabilities =
                        connectivityManager.getNetworkCapabilities(activeNetwork);

                if (capabilities != null) {
                    // 네트워크가 모바일 데이터를 통해 연결된 상태인지 확인
                    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                }
            }
        }
        return false;
    }
}
