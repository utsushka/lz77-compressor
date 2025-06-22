package com.lz77.model;

import com.lz77.algorithm.LZ77Compressor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Compression Result Tests")
class CompressionResultTest {

    @Test
    @DisplayName("should calculate correct compression ratio for normal data")
    void compressionRatio_NormalData_ReturnsCorrectValue() {
        byte[] data = "abracadabra".getBytes(); // 11 bytes
        List<Token> tokens = new LZ77Compressor().compress(data);

        CompressionResult result = new CompressionResult(data, tokens);

        assertAll(
                () -> assertTrue(result.compressionRatio() > 0.5, "Ratio should be > 0.5"),
                () -> assertTrue(result.compressionRatio() < 0.7, "Ratio should be < 0.7"),
                () -> assertFalse(result.isEffectiveCompression(), "Should not be effective compression")
        );
    }

    @Test
    @DisplayName("should calculate correct compression ratio for highly compressible data")
    void compressionRatio_HighlyCompressibleData_ReturnsCorrectValue() {
        byte[] data = "aaaaaaaaaa".getBytes(); // 10 bytes
        List<Token> tokens = new LZ77Compressor().compress(data);

        CompressionResult result = new CompressionResult(data, tokens);

        assertAll(
                () -> assertTrue(result.compressionRatio() > 1.5, "Ratio should be > 1.5"),
                () -> assertTrue(result.isEffectiveCompression(), "Should be effective compression"),
                () -> assertTrue(result.getCompressionPercentage() > 30, "Percentage should be > 30%")
        );
    }

    @Test
    @DisplayName("should handle empty input data")
    void compressionRatio_EmptyData_ReturnsZero() {
        CompressionResult result = new CompressionResult(new byte[0], List.of());

        assertAll(
                () -> assertEquals(0.0, result.compressionRatio(), "Ratio should be 0"),
                () -> assertEquals(0.0, result.getCompressionPercentage(), "Percentage should be 0"),
                () -> assertFalse(result.isEffectiveCompression(), "Should not be effective compression")
        );
    }

    @ParameterizedTest
    @MethodSource("provideSummaryTestCases")
    @DisplayName("should generate correct summary format")
    void getSummary_VariousCases_ReturnsCorrectFormat(byte[] data, List<Token> tokens, String expectedContains) {
        CompressionResult result = new CompressionResult(data, tokens);
        String summary = result.getSummary();
        assertTrue(summary.contains(expectedContains),
                "Summary should contain: " + expectedContains);
    }

    @Test
    @DisplayName("should store and return compression time")
    void compressionTime_WithTime_ReturnsCorrectValue() {
        byte[] data = "sample".getBytes();
        long expectedTime = 42L;
        CompressionResult result = new CompressionResult(data, List.of(), 1.5, expectedTime);

        assertAll(
                () -> assertEquals(1.5, result.compressionRatio(), "Ratio should match"),
                () -> assertEquals(expectedTime, result.compressionTime(), "Time should match")
        );
    }

    private static Stream<Arguments> provideSummaryTestCases() {
        return Stream.of(
                arguments(
                        "test".getBytes(),
                        List.of(
                                new Token(0, 0, (byte)'t'),
                                new Token(0, 0, (byte)'e'),
                                new Token(0, 0, (byte)'s'),
                                new Token(0, 0, (byte)'t')
                        ),
                        "Original: 4 bytes, Compressed: ~4 tokens (12 bytes)"
                ),
                arguments(
                        "hello".getBytes(),
                        List.of(
                                new Token(0, 0, (byte)'h'),
                                new Token(0, 0, (byte)'e'),
                                new Token(1, 1, (byte)'l')
                        ),
                        "Original: 5 bytes, Compressed: ~3 tokens (9 bytes)"
                )
        );
    }
}