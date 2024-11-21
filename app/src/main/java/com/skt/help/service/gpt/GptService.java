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

    public String process(String recordText, String location, String customInputText) {
        Map<String, String> data = new HashMap<>();
        data.put("recordText", recordText);
        data.put("location", location);
        data.put("customInputText", customInputText);
        String prompt = promptService.generatePrompt(data);
        return openAIRepository.chat(prompt);
    }
}
