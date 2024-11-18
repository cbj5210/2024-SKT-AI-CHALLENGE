package com.skt.help.gpt.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skt.help.gpt.api.OpenAIApi;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenAIRepository {
    private final OpenAIApi openAIApi;
    private final ObjectMapper objectMapper;

    public OpenAIRepository() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        openAIApi = retrofit.create(OpenAIApi.class);
        objectMapper = new ObjectMapper();
    }

    public String chat(String prompt) {
        try {
            Call<ResponseBody> call = openAIApi.sendMessageRaw(makeBody(prompt));
            Response<ResponseBody> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                JsonNode responseJson = objectMapper.readTree(response.body().string());
                JsonNode messageNode = responseJson.path("choices").get(0).path("message").path("content");
                return messageNode.asText(); // content 반환
            } else {
                throw new RuntimeException("API Error: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch response from API", e);
        }


    }

    private RequestBody makeBody(String question) {
        String format = """
                {
                     "model": "gpt-4o-mini",
                     "messages": [
                        {
                          "role": "system",
                          "content": [
                            {
                              "type": "text",
                              "text": "You are an employee working in the emergency operations center."
                            }
                          ]
                        },
                       {
                         "role": "user",
                         "content": [
                           {
                             "type": "text",
                             "text": "%s"
                           }
                         ]
                       }
                     ],
                     "temperature": 1,
                     "max_tokens": 256,
                     "top_p": 1,
                     "frequency_penalty": 0,
                     "presence_penalty": 0,
                     "response_format": {
                         "type": "text"
                      }
                   }
                """;
        String body = String.format(format, question);
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body);
    }
}
