package com.lz77.algorithm;

import com.lz77.model.Token;
import java.util.ArrayList;
import java.util.List;

public class LZ77Compressor {
    public static final int WINDOW_SIZE = 1024;
    private static final int LOOKAHEAD_BUFFER_SIZE = 256;

    public List<Token> compress(byte[] input) {
        List<Token> tokens = new ArrayList<>();
        int pos = 0;

        while (pos < input.length) {
            int maxLength = 0;
            int bestOffset = 0;
            int end = Math.min(pos + LOOKAHEAD_BUFFER_SIZE, input.length);

            // Поиск наилучшего совпадения в скользящем окне
            for (int offset = 1; offset <= Math.min(WINDOW_SIZE, pos); offset++) {
                int length = 0;
                while (pos + length < end
                        && input[pos + length] == input[pos - offset + length]) {
                    length++;
                }

                if (length > maxLength) {
                    maxLength = length;
                    bestOffset = offset;
                }
            }

            if (maxLength > 0) {
                // Не добавляем nextChar если это конец данных
                byte nextChar = pos + maxLength < input.length ? input[pos + maxLength] : 0;
                if (nextChar != 0 || pos + maxLength < input.length) {
                    tokens.add(new Token(bestOffset, maxLength, nextChar));
                } else {
                    tokens.add(new Token(bestOffset, maxLength, (byte)0));
                }
                pos += maxLength + (nextChar != 0 ? 1 : 0);
            } else {
                tokens.add(new Token(0, 0, input[pos]));
                pos++;
            }
        }

        return tokens;
    }
}