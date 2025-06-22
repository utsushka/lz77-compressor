package com.lz77.algorithm;

import com.lz77.model.Token;
import java.util.List;

public class LZ77Decompressor {

    public byte[] decompress(List<Token> tokens) {
        int outputSize = calculateOutputSize(tokens);
        byte[] output = new byte[outputSize];
        int outputPos = 0;

        for (Token token : tokens) {
            if (token.offset() == 0) {
                output[outputPos++] = token.nextChar();
            } else {
                // Копирование совпадающей последовательности
                int startPos = outputPos - token.offset();
                for (int i = 0; i < token.length(); i++) {
                    output[outputPos++] = output[startPos + i];
                }
                // Добавляем следующий символ, если он есть (даже если нулевой)
                if (outputPos < output.length && token.nextChar() != 0) {
                    output[outputPos++] = token.nextChar();
                }
            }
        }
        return output;
    }

    private int calculateOutputSize(List<Token> tokens) {
        int size = 0;
        for (Token token : tokens) {
            size += token.length();
            if (token.offset() == 0 || token.nextChar() != 0) {
                size += 1;
            }
        }
        return size;
    }
}