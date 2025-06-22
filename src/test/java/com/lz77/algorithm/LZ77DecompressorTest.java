package com.lz77.algorithm;

import com.lz77.model.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("LZ77 Decompressor Tests")
class LZ77DecompressorTest {
    private LZ77Decompressor decompressor;

    @BeforeEach
    void setUp() {
        decompressor = new LZ77Decompressor();
    }

    @Test
    @DisplayName("should decompress empty input to empty array")
    void decompress_EmptyInput_ReturnsEmptyArray() {
        byte[] result = decompressor.decompress(List.of());
        assertEquals(0, result.length);
    }

    @ParameterizedTest
    @MethodSource("decompressionTestCases")
    @DisplayName("should correctly decompress tokens to original data")
    void decompress_ValidTokens_ReturnsCorrectData(List<Token> tokens, String expected) {
        byte[] result = decompressor.decompress(tokens);
        assertArrayEquals(expected.getBytes(), result);
    }

    private static Stream<Arguments> decompressionTestCases() {
        return Stream.of(
                arguments(
                        List.of(
                                new Token(0, 0, (byte)'a'),
                                new Token(0, 0, (byte)'b'),
                                new Token(0, 0, (byte)'r'),
                                new Token(3, 1, (byte)'c'),
                                new Token(2, 1, (byte)'d'),
                                new Token(7, 4, (byte)0)
                        ),
                        "abracadabra"
                ),
                arguments(
                        List.of(
                                new Token(0, 0, (byte)'a'),
                                new Token(0, 0, (byte)'b'),
                                new Token(0, 0, (byte)'c'),
                                new Token(3, 6, (byte)0)
                        ),
                        "abcabcabc"
                ),
                arguments(
                        List.of(
                                new Token(0, 0, (byte)'H'),
                                new Token(0, 0, (byte)'i'),
                                new Token(0, 0, (byte)'!')
                        ),
                        "Hi!"
                ),
                arguments(
                        List.of(
                                new Token(0, 0, (byte)'A'),
                                new Token(1, 1, (byte)0)  // Тест на нулевой символ
                        ),
                        "AA"
                ),
                arguments(
                        List.of(),  // Пустой ввод
                        ""
                )
        );
    }

    @Test
    @DisplayName("should handle binary data decompression")
    void decompress_BinaryData_ReturnsCorrectBytes() {
        List<Token> tokens = List.of(
                new Token(0, 0, (byte)0x01),
                new Token(0, 0, (byte)0x02),
                new Token(2, 1, (byte)0xFF)
        );

        byte[] expected = new byte[]{0x01, 0x02, 0x01, (byte)0xFF};
        byte[] result = decompressor.decompress(tokens);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("should handle unicode characters")
    void decompress_UnicodeCharacters_ReturnsCorrectBytes() {
        String testString = "тест";
        byte[] originalBytes = testString.getBytes(StandardCharsets.UTF_8);

        // Создаем токены для каждого байта UTF-8 представления
        List<Token> tokens = new ArrayList<>();
        for (byte b : originalBytes) {
            tokens.add(new Token(0, 0, b));
        }

        byte[] result = decompressor.decompress(tokens);
        assertArrayEquals(originalBytes, result);
    }
}