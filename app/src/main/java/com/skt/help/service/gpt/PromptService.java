package com.skt.help.service.gpt;

import android.content.Context;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.skt.help.R;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;

public class PromptService {

    private final Mustache mustache;

    public PromptService(Context context) {
        try (InputStream inputStream = context.getResources().openRawResource(R.raw.prompt);
             InputStreamReader reader = new InputStreamReader(inputStream)) {

            MustacheFactory mf = new DefaultMustacheFactory();
            this.mustache = mf.compile(reader, "template");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load Mustache template", e);
        }
    }

    public String generatePrompt(Map<String, String> data) {
        try (StringWriter writer = new StringWriter()) {
            mustache.execute(writer, data).flush();
            return writer.toString().replace("\n", "\\\\n");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
