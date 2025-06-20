package com.lz77.model;

/**
 * Класс, представляющий токен LZ77
 * @param offset смещение назад в буфере поиска
 * @param length длина совпадающей последовательности
 * @param nextChar следующий символ после совпадения
 */
public record Token(int offset, int length, byte nextChar) {

    public Token {
        if (offset < 0) throw new IllegalArgumentException("Offset cannot be negative");
        if (length < 0) throw new IllegalArgumentException("Length cannot be negative");
    }

    /*
    @Override
    public String toString() {
        return String.format("(%d,%d,%s)", offset, length,
                nextChar == 0 ? "null" : (char)nextChar);
    }
    */
}