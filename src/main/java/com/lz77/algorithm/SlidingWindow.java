package com.lz77.algorithm;

import java.util.Arrays;

/**
 * Класс, реализующий скользящее окно для алгоритма LZ77
 */
public class SlidingWindow {
    private final byte[] data;
    private final int windowSize;
    private final int lookaheadSize;
    private int currentPosition;

    /**
     * @param data входные данные
     * @param windowSize размер окна поиска
     * @param lookaheadSize размер буфера предпросмотра
     */
    public SlidingWindow(byte[] data, int windowSize, int lookaheadSize) {
        this.data = data;
        this.windowSize = windowSize;
        this.lookaheadSize = lookaheadSize;
        this.currentPosition = 0;
    }

    /**
     * Поиск наиболее длинного совпадения в буфере поиска
     * @return массив из 3 элементов: [offset, length, nextChar]
     */
    public int[] findLongestMatch() {
        int maxLength = 0;
        int bestOffset = 0;
        byte nextChar = 0;

        if (currentPosition >= data.length) {
            return new int[]{0, 0, 0};
        }

        int searchStart = Math.max(0, currentPosition - windowSize);
        int lookaheadEnd = Math.min(currentPosition + lookaheadSize, data.length);
        int maxPossibleLength = lookaheadEnd - currentPosition;

        // Ограничиваем максимальную длину совпадения размером буфера предпросмотра
        for (int offset = 1; offset <= currentPosition - searchStart; offset++) {
            int length = 0;
            while (length < maxPossibleLength &&
                    currentPosition + length < data.length &&
                    data[currentPosition + length] == data[currentPosition - offset + length]) {
                length++;
            }

            if (length > maxLength) {
                maxLength = length;
                bestOffset = offset;
            }
        }

        // Определяем следующий символ после совпадения
        if (currentPosition + maxLength < data.length) {
            nextChar = data[currentPosition + maxLength];
        } else {
            nextChar = 0;
        }

        return new int[]{bestOffset, maxLength, nextChar};
    }

    /**
     * Перемещает окно вперед на указанное количество символов
     * @param shift количество символов для смещения
     */
    public void advance(int shift) {
        currentPosition += shift;
        currentPosition = Math.min(currentPosition, data.length);
    }

    /**
     * Проверяет, достигнут ли конец данных
     */
    public boolean hasMoreData() {
        return currentPosition < data.length;
    }

    /**
     * Возвращает текущую позицию в данных
     */
    public int getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Возвращает текущее содержимое буфера поиска
     */
    public byte[] getSearchBuffer() {
        int start = Math.max(0, currentPosition - windowSize);
        return Arrays.copyOfRange(data, start, currentPosition);
    }

    /**
     * Возвращает текущее содержимое буфера предпросмотра
     */
    public byte[] getLookaheadBuffer() {
        int end = Math.min(currentPosition + lookaheadSize, data.length);
        return Arrays.copyOfRange(data, currentPosition, end);
    }
}