package com.lz77.algorithm;

import com.lz77.model.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("LZ77 Compressor Tests")
class LZ77CompressorTest {
    private LZ77Compressor compressor;
    private LZ77Decompressor decompressor;

    @BeforeEach
    void setUp() {
        compressor = new LZ77Compressor();
        decompressor = new LZ77Decompressor();
    }

    @Test
    @DisplayName("should return empty list for empty input")
    void compress_EmptyInput_ReturnsEmptyList() {
        List<Token> tokens = compressor.compress(new byte[0]);
        assertTrue(tokens.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "b", "1", "\n"})
    @DisplayName("should handle single character input")
    void compress_SingleCharacter_ReturnsSingleToken(String inputStr) {
        byte[] input = inputStr.getBytes();
        List<Token> tokens = compressor.compress(input);

        assertEquals(1, tokens.size());
        assertEquals(new Token(0, 0, input[0]), tokens.get(0));
    }

    @Test
    @DisplayName("should handle input without repetitions")
    void compress_NoRepetitions_ReturnsLiteralTokens() {
        byte[] input = "abcdef".getBytes();
        List<Token> tokens = compressor.compress(input);

        assertEquals(input.length, tokens.size());
        for (int i = 0; i < input.length; i++) {
            assertEquals(new Token(0, 0, input[i]), tokens.get(i));
        }
    }

    @ParameterizedTest
    @MethodSource("repetitionTestCases")
    @DisplayName("should detect repetitions correctly")
    void compress_WithRepetitions_ReturnsCorrectTokens(byte[] input, List<Token> expectedTokens) {
        List<Token> actualTokens = compressor.compress(input);
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    @DisplayName("should handle window size limitations")
    void compress_LargeInput_RespectsWindowSize() {
        String longString = "a".repeat(2000) + "b";
        byte[] input = longString.getBytes();

        List<Token> tokens = compressor.compress(input);

        assertAll(
                () -> assertTrue(tokens.size() > 2),
                () -> assertEquals(new Token(0, 0, (byte)'a'), tokens.get(0)),
                () -> assertTrue(tokens.get(tokens.size() - 1).offset() <= LZ77Compressor.WINDOW_SIZE),
                () -> assertEquals('b', tokens.get(tokens.size() - 1).nextChar())
        );
    }

    @Test
    @DisplayName("should handle binary data")
    void compress_BinaryData_ReturnsCorrectTokens() {
        byte[] input = new byte[]{0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3};
        List<Token> expected = List.of(
                new Token(0, 0, (byte)0),
                new Token(0, 0, (byte)1),
                new Token(2, 4, (byte)2),
                new Token(0, 0, (byte)3),
                new Token(2, 4, (byte)0)
        );

        List<Token> actual = compressor.compress(input);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("roundTripTestCases")
    @DisplayName("should correctly round-trip compress and decompress")
    void compressThenDecompress_ReturnsOriginalInput(String inputStr) {
        byte[] input = inputStr.getBytes();
        List<Token> tokens = compressor.compress(input);
        byte[] output = decompressor.decompress(tokens);
        assertArrayEquals(input, output, "Failed for: " + inputStr);
    }

    private static Stream<Arguments> repetitionTestCases() {
        return Stream.of(
                arguments(
                        "ababab".getBytes(),
                        List.of(
                                new Token(0, 0, (byte)'a'),
                                new Token(0, 0, (byte)'b'),
                                new Token(2, 4, (byte)0)
                        )
                ),
                arguments(
                        "abracadabra".getBytes(),
                        List.of(
                                new Token(0, 0, (byte)'a'),
                                new Token(0, 0, (byte)'b'),
                                new Token(0, 0, (byte)'r'),
                                new Token(3, 1, (byte)'c'),
                                new Token(2, 1, (byte)'d'),
                                new Token(7, 4, (byte)0)
                        )
                ),
                arguments(
                        "aaaaa".getBytes(),
                        List.of(
                                new Token(0, 0, (byte)'a'),
                                new Token(1, 4, (byte)0)
                        )
                )
        );
    }

    private static Stream<Arguments> roundTripTestCases() {
        return Stream.of(
                arguments("abracadabra"),
                arguments("test test test"),
                arguments("aaaaaaaaaa"),
                arguments("abcabcabcabc"),
                arguments("The quick brown fox jumps over the lazy dog"),
                arguments("") // edge case: empty string
        );
    }
}