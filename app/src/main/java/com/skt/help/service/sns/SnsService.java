package com.skt.help.service.sns;

import com.skt.help.repository.TelegramRepository;

public class SnsService {
    private final TelegramRepository telegramRepository;

    public SnsService() {
        this.telegramRepository = new TelegramRepository();
    }

    public void sendTelegramMessage(String message) {
        telegramRepository.sendMessage(message);
    }
}
