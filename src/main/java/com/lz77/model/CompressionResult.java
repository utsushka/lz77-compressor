package com.lz77.model;

import java.util.List;

/**
 * Класс для хранения результатов сжатия данных алгоритмом LZ77
 * @param originalData исходные данные
 * @param compressedTokens список токенов после сжатия
 * @param compressionRatio коэффициент сжатия
 * @param compressionTime время выполнения сжатия в миллисекундах
 */
public record CompressionResult(
        byte[] originalData,
        List<Token> compressedTokens,
        double compressionRatio,
        long compressionTime
) {
    /**
     * Вспомогательный конструктор без времени выполнения
     */
    public CompressionResult(byte[] originalData, List<Token> compressedTokens) {
        this(originalData, compressedTokens,
                calculateCompressionRatio(originalData, compressedTokens),
                0);
    }

    /**
     * Вычисляет коэффициент сжатия
     * @return отношение размера исходных данных к размеру сжатых данных
     */
    private static double calculateCompressionRatio(byte[] originalData, List<Token> compressedTokens) {
        if (originalData == null || originalData.length == 0) {
            return 0.0;
        }

        // Приблизительный размер сжатых данных (каждый токен занимает ~3 байта)
        int compressedSize = compressedTokens.size() * 3;
        return (double) originalData.length / compressedSize;
    }

    /**
     * @return процент сжатия (0-100%)
     */
    public double getCompressionPercentage() {
        if (compressionRatio == 0.0) {
            return 0.0;
        }
        return 100 - (100 / compressionRatio);
    }

    /**
     * @return true, если сжатие было эффективным (коэффициент > 1)
     */
    public boolean isEffectiveCompression() {
        return compressionRatio > 1.0;
    }

    /**
     * @return человеко-читаемое описание результатов сжатия
     */
    public String getSummary() {
        return String.format(
                "Original: %d bytes, Compressed: ~%d tokens (%d bytes), Ratio: %.2f:1 (%.1f%%)",
                originalData.length,
                compressedTokens.size(),
                compressedTokens.size() * 3,  // теперь используется %d вместо %.1f
                compressionRatio,
                getCompressionPercentage()
        );
    }
}