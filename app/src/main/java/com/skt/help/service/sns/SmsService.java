package com.skt.help.service.sns;

import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.List;

public class SmsService {

    public SmsService() {
    }

    public void sendSmsMessage(String number, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();

            List<String> chunkList = splitString(message, 60);

            for(String chunk : chunkList) {
                smsManager.sendTextMessage(number, null, chunk, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> splitString(String input, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int length = input.length();

        for (int i = 0; i < length; i += chunkSize) {
            int end = Math.min(length, i + chunkSize);
            chunks.add(input.substring(i, end));
        }

        return chunks;
    }
}
