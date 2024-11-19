package com.skt.help.service.gpt;

import android.content.Context;

import com.skt.help.repository.OpenAIRepository;

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
