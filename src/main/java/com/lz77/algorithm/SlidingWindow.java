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

        int searchStart = Math.max(0, currentPosition - windowSize);
        int searchEnd = currentPosition;
        int lookaheadEnd = Math.min(currentPosition + lookaheadSize, data.length);

        // Оптимизация: прекращаем поиск, если дошли до конца данных
        if (currentPosition >= data.length) {
            return new int[]{0, 0, 0};
        }

        for (int i = searchStart; i < searchEnd; i++) {
            int length = 0;

            // Сравниваем символы в буфере поиска и предпросмотра
            while (currentPosition + length < lookaheadEnd
                    && i + length < searchEnd
                    && data[i + length] == data[currentPosition + length]) {
                length++;
            }

            // Обновляем лучшее совпадение
            if (length > maxLength) {
                maxLength = length;
                bestOffset = currentPosition - i;

                // Получаем следующий символ после совпадения
                if (currentPosition + length < data.length) {
                    nextChar = data[currentPosition + length];
                } else {
                    nextChar = 0;
                }
            }
        }

        return new int[]{bestOffset, maxLength, nextChar & 0xFF};
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