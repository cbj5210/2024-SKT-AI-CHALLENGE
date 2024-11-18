package com.skt.help.gpt.service;

import android.content.Context;

import com.skt.help.gpt.repository.OpenAIRepository;

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
        data.put("speech", speech);
        String prompt = promptService.generatePrompt(data);
        return openAIRepository.chat(prompt);
    }
}
