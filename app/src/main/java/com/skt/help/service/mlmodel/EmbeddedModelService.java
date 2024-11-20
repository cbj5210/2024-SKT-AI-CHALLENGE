package com.skt.help.service.mlmodel;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class EmbeddedModelService {
    private static final String MODEL_FILE_NAME = "koelectra_small_emergency_finetuned.tflite";
    private static final int MAX_RETRY_ATTEMPTS = 3; // 최대 재시도 횟수
    private final Context context;
    public EmbeddedModelService(Context context) {
        this.context = context;
    }

    private MappedByteBuffer loadModelFromAssets(String modelPath) throws Exception {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
        FileChannel fileChannel = new FileInputStream(fileDescriptor.getFileDescriptor()).getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public boolean isEmergency(String sentence) {
        int attempts = 0;
        Interpreter interpreter = null;
        while (attempts < MAX_RETRY_ATTEMPTS) {
            attempts++;
            try {
                MappedByteBuffer modelFile = loadModelFromAssets(MODEL_FILE_NAME);
                interpreter = new Interpreter(modelFile);

                // 모델 질의
                float[] prediction = predict(interpreter, sentence);

                // 예측 결과 출력
                Log.d("질의어 ", sentence);
                Log.d("예측", "Prediction: " + prediction[0] + ", " + prediction[1]);


                // Softmax 적용
                float[] probabilities = softmax(prediction);

            // 결과 출력
                Log.d("결과", "위급상황 확률 : " + probabilities[1]);
                if (probabilities[0] > probabilities[1]) {
                    return false;
                } else {
                    return true;
                }

            } catch (Exception e) {
                Log.d("초기화 실패", "재시도 횟수 : " + attempts);
            } finally {
                if (interpreter != null) {
                    interpreter.close();
                    System.gc();
                }
            }
        }
        return false;
    }

    public float[] predict(Interpreter interpreter, String inputText) {
            int[] inputShape = interpreter.getInputTensor(0).shape(); // [1, 128]
            int sequenceLength = inputShape[1]; // 시퀀스 길이

        EmergencyTokenizer.TokenizedOutput tokenizedInput = EmergencyTokenizer.tokenize(inputText);

        int[] inputIds = tokenizedInput.getInputIds();
        int[] attentionMasks = tokenizedInput.getAttentionMasks();

        // ByteBuffer로 변환 (배치 차원 포함)
        ByteBuffer inputBuffer = allocateByteBuffer(inputIds, sequenceLength);
        ByteBuffer attentionBuffer = allocateByteBuffer(attentionMasks, sequenceLength);

        Object[] inputs = {inputBuffer, attentionBuffer};

        float[][] outputData = new float[1][2]; // 출력 크기와 맞춤 (배치 크기 1, 클래스 2)
        Map<Integer, Object> outputMap = new HashMap<>();
        outputMap.put(0, outputData);

        // 모델 실행
        interpreter.runForMultipleInputsOutputs(inputs, outputMap);

        return outputData[0]; // 배치 크기가 1이므로 첫 번째 결과를 가져옴
    }

    private ByteBuffer allocateByteBuffer(int[] inputs, int sequenceLength) {
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4 * sequenceLength);
        inputBuffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < sequenceLength; i++) {
            inputBuffer.putInt(i < inputs.length ? inputs[i] : 0); // 패딩 추가
        }
        return inputBuffer;
    }

    private float[] softmax(float[] logits) {
        float[] probabilities = new float[logits.length];
        float sum = 0.0f;

        for (float logit : logits) {
            sum += Math.exp(logit);
        }

        for (int i = 0; i < logits.length; i++) {
            probabilities[i] = (float) Math.exp(logits[i]) / sum;
        }

        return probabilities;
    }
}
