package com.skt.help.service.gpt;

import android.content.Context;

import com.skt.help.repository.OpenAIRepository;
import com.skt.help.util.DateUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class GptService {
    private final PromptService promptService;
    private final OpenAIRepository openAIRepository;

    public GptService(Context context) {
        this.openAIRepository = new OpenAIRepository();
        this.promptService = new PromptService(context);
    }

    public String process(String speech) {
        Map<String, String> data = new HashMap<>();
        data.put("emergencyType", "구조 요청");
        data.put("dateTime", DateUtils.getCurrentDateTime());
        data.put("latestPosition", "서울시 강남구 테헤란로 123");
        data.put("route", "용인시 수지구 동천동 → 성남시 분당구 정자동 → 서울시 강남구 테헤란로 123\n");
        data.put("parentPhoneNumber", "010-2222-3333");
        String prompt = promptService.generatePrompt(data);
        return openAIRepository.chat(prompt);
    }
}
