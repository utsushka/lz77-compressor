package com.lz77.util;

import com.lz77.model.Token;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Data Validation Tests")
class ValidationTest {

    @ParameterizedTest(name = "should {0} for token validation")
    @MethodSource("provideTokenValidationCases")
    @DisplayName("validate tokens correctly")
    void validateTokens_VariousCases(String description, List<Token> tokens, boolean shouldPass) {
        if (shouldPass) {
            assertDoesNotThrow(() -> Validation.validateTokens(tokens));
        } else {
            assertThrows(IllegalStateException.class,
                    () -> Validation.validateTokens(tokens));
        }
    }

    @ParameterizedTest(name = "should {0} for data validation")
    @MethodSource("provideDataValidationCases")
    @DisplayName("validate decompressed data correctly")
    void validateDecompressedData_VariousCases(
            String description, List<Token> tokens, byte[] data, boolean shouldPass) {
        if (shouldPass) {
            assertDoesNotThrow(() -> Validation.validateDecompressedData(tokens, data));
        } else {
            assertThrows(IllegalStateException.class,
                    () -> Validation.validateDecompressedData(tokens, data));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 100, 100 * 1024 * 1024}) // 0B, 100B, 100MB
    @DisplayName("should accept valid input data sizes")
    void validateInputData_ValidSizes_DoesNotThrow(int size) {
        byte[] data = new byte[size];
        assertDoesNotThrow(() -> Validation.validateInputData(data));
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("should reject invalid input data")
    void validateInputData_InvalidData_ThrowsException(byte[] invalidData) {
        assertThrows(IllegalArgumentException.class,
                () -> Validation.validateInputData(invalidData));
    }

    @Test
    @DisplayName("should reject oversized input data")
    void validateInputData_OversizedData_ThrowsException() {
        byte[] largeData = new byte[101 * 1024 * 1024]; // 101MB
        assertThrows(IllegalArgumentException.class,
                () -> Validation.validateInputData(largeData));
    }

    private static Stream<Arguments> provideTokenValidationCases() {
        return Stream.of(
                arguments("accept valid tokens",
                        List.of(
                                new Token(0, 0, (byte)'a'),
                                new Token(3, 2, (byte)'b')
                        ),
                        true),
                arguments("accept empty list",
                        List.of(),
                        true)
        );
    }

    private static Stream<Arguments> provideDataValidationCases() {
        return Stream.of(
                arguments("accept matching data",
                        List.of(new Token(0, 0, (byte)'a')),
                        "a".getBytes(),
                        true),
                arguments("reject empty data with tokens",
                        List.of(new Token(0, 0, (byte)'a')),
                        new byte[0],
                        false),
                arguments("accept empty data with no tokens",
                        List.of(),
                        new byte[0],
                        true),
                arguments("reject data with no tokens",
                        List.of(),
                        new byte[1],
                        false)
        );
    }
}