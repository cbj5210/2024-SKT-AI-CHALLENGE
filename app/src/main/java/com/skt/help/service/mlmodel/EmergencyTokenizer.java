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
        vocab.put("[UNK]", 0); // Unknown Token
        vocab.put("[CLS]", 1); // Classifier Token
        vocab.put("[SEP]", 2); // Separator Token
        vocab.put("사고", 3);
        vocab.put("사고가", 4);
        vocab.put("사고로", 5);
        vocab.put("사고났어요", 6);
        vocab.put("사고가 났어요", 7);
        vocab.put("위험", 8);
        vocab.put("위험합니다", 9);
        vocab.put("위험해요", 10);
        vocab.put("긴급", 11);
        vocab.put("긴급합니다", 12);
        vocab.put("긴급한", 13);
        vocab.put("구조", 14);
        vocab.put("구조가", 15);
        vocab.put("구조요청", 16);
        vocab.put("구조해주세요", 17);
        vocab.put("도와주세요", 18);
        vocab.put("도움", 19);
        vocab.put("도움이", 20);
        vocab.put("도움이 필요합니다", 21);
        vocab.put("살려주세요", 22);
        vocab.put("살려주십시오", 23);
        vocab.put("도와주십시오", 24);
        vocab.put("도움 필요", 25);
        vocab.put("구해주세요", 26);
        vocab.put("도움 요청", 27);
        vocab.put("응급", 28);
        vocab.put("응급상황", 29);
        vocab.put("응급입니다", 30);
        vocab.put("응급환자", 31);
        vocab.put("호흡곤란", 32);
        vocab.put("숨을 못 쉬겠어요", 33);
        vocab.put("숨막혀요", 34);
        vocab.put("출혈", 35);
        vocab.put("피가 나요", 36);
        vocab.put("쓰러짐", 37);
        vocab.put("쓰러졌어요", 38);
        vocab.put("떨어짐", 39);
        vocab.put("떨어졌어요", 40);
        vocab.put("폭행", 41);
        vocab.put("폭행을 당했어요", 42);
        vocab.put("폭력", 43);
        vocab.put("위협", 44);
        vocab.put("위협받고 있어요", 45);
        vocab.put("납치", 46);
        vocab.put("납치당했어요", 47);
        vocab.put("강도", 48);
        vocab.put("도둑", 49);
        vocab.put("침입", 50);
        vocab.put("도망쳐요", 51);
        vocab.put("도주", 52);
        vocab.put("지진", 53);
        vocab.put("지진 발생", 54);
        vocab.put("홍수", 55);
        vocab.put("홍수가 났어요", 56);
        vocab.put("태풍", 57);
        vocab.put("태풍으로", 58);
        vocab.put("산사태", 59);
        vocab.put("산사태가 발생", 60);
        vocab.put("붕괴", 61);
        vocab.put("침수", 62);
        vocab.put("물이 찼어요", 63);
        vocab.put("강풍", 64);
        vocab.put("도움 요청", 65);
        vocab.put("위험합니다", 66);
        vocab.put("긴급 상황입니다", 67);
        vocab.put("SOS", 68);
        vocab.put("구조 신호", 69);
        vocab.put("살려달라", 70);
        vocab.put("제발", 71);
        vocab.put("도와달라", 72);
        vocab.put("살려달라", 73);

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
