package com.skt.help.service.mlmodel;

import android.util.Log;

import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.phrase_extractor.KoreanPhraseExtractor;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import scala.collection.Seq;

public class EmergencyTokenizer {
    private static final Map<String, Integer> vocab = new HashMap<>();

    static {
        // 위급상황 단어 사전
        vocab.put("[UNK]", 1);
        vocab.put("[CLS]", 2);
        vocab.put("[SEP]", 3);
        vocab.put("사고", 6558);
        vocab.put("위험", 6793);
        vocab.put("추락", 9885);
        vocab.put("도움", 6750);
        vocab.put("납치", 10858);
        vocab.put("폭행", 9150);
        vocab.put("응급", 10731);
        vocab.put("출혈", 16368);
        vocab.put("피", 3747);
        vocab.put("긴급", 8725);
        vocab.put("도움", 6750);
        vocab.put("위협", 7625);
        vocab.put("강도", 9226);
        vocab.put("침입", 11990);
        vocab.put("도둑", 11722);
        vocab.put("구조", 6549);
        vocab.put("제발", 11777);

    }

    // 입력 텍스트를 input_ids와 attention_mask로 변환
    public static TokenizedOutput tokenize(String inputText) {
        List<Integer> inputIds = new ArrayList<>();
        List<Integer> attentionMask = new ArrayList<>();

        inputIds.add(vocab.get("[CLS]"));
        attentionMask.add(1);

        // OpenKoreanTextProcessor 라이브러리 이용하여 정규화 및 어구 추출
        List<String> phraseList = extractPhraseList(inputText);
        Log.d("어구추출", phraseList.toString());
        for (String phrase : phraseList) {
            inputIds.add(vocab.getOrDefault(phrase, vocab.get("[UNK]")));
            attentionMask.add(1);
        }

        inputIds.add(vocab.get("[SEP]"));
        attentionMask.add(1);

        while (inputIds.size() < 10) {
            inputIds.add(vocab.get("[UNK]"));
            attentionMask.add(0);
        }

        return new TokenizedOutput(inputIds, attentionMask);
    }

    private static List<String> extractPhraseList(String inputText) {
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(inputText);
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);

        return OpenKoreanTextProcessorJava.extractPhrases(tokens, true, true)
                .stream()
                .map(KoreanPhraseExtractor.KoreanPhrase::text)
                .collect(Collectors.toList());
    }

    public static class TokenizedOutput {
        private final int[] inputIds;
        private final int[] attentionMasks;

        public TokenizedOutput(List<Integer> inputIdsList, List<Integer> attentionMaskList) {
            this.inputIds = inputIdsList.stream().mapToInt(i -> i).toArray();;
            this.attentionMasks = attentionMaskList.stream().mapToInt(i -> i).toArray();;
        }

        public int[] getInputIds() {
            return inputIds;
        }

        public int[] getAttentionMasks() {
            return attentionMasks;
        }
    }
}
