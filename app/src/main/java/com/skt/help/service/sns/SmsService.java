package com.skt.help.service.sns;

import android.telephony.SmsManager;

public class SmsService {

    public SmsService() {
    }

    public void sendSmsMessage(String number, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
