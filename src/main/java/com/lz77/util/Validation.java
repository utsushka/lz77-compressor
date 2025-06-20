package com.lz77.util;

import com.lz77.model.Token;
import java.util.List;

/**
 * Класс для валидации входных и выходных данных
 */
public class Validation {

    /**
     * Проверка корректности токенов после чтения из файла
     */
    public static void validateTokens(List<Token> tokens) {
        if (tokens == null) {
            throw new IllegalArgumentException("Tokens list cannot be null");
        }

        for (Token token : tokens) {
            if (token.offset() < 0) {
                throw new IllegalStateException("Invalid token: negative offset");
            }
            if (token.length() < 0) {
                throw new IllegalStateException("Invalid token: negative length");
            }
        }
    }

    /**
     * Проверка, что распакованные данные соответствуют исходным токенам
     */
    public static void validateDecompressedData(List<Token> tokens, byte[] decompressedData) {
        if (tokens == null || decompressedData == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        // Проверка, что токены действительно могли создать такие данные
        // (это упрощенная проверка, полная проверка потребовала бы повторной компрессии)
        if (decompressedData.length == 0 && !tokens.isEmpty()) {
            throw new IllegalStateException("Decompressed data is empty but tokens exist");
        }

        if (decompressedData.length > 0 && tokens.isEmpty()) {
            throw new IllegalStateException("Decompressed data exists but no tokens provided");
        }
    }

    /**
     * Проверка входных данных перед сжатием
     */
    public static void validateInputData(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Input data cannot be null");
        }

        if (data.length > 100 * 1024 * 1024) { // 100MB
            throw new IllegalArgumentException("Input data too large (max 100MB)");
        }
    }
}