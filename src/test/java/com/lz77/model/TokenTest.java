package com.lz77.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Token Model Tests")
class TokenTest {

    @Test
    @DisplayName("should create token with valid parameters")
    void createToken_ValidParameters_Success() {
        Token token = new Token(10, 5, (byte)'a');

        assertAll(
                () -> assertEquals(10, token.offset()),
                () -> assertEquals(5, token.length()),
                () -> assertEquals('a', (char)token.nextChar())
        );
    }

    @ParameterizedTest(name = "should throw exception for invalid offset {0}")
    @ValueSource(ints = {-1, -10, Integer.MIN_VALUE})
    void createToken_InvalidOffset_ThrowsException(int invalidOffset) {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Token(invalidOffset, 5, (byte)'a')
        );
        assertEquals("Offset cannot be negative", exception.getMessage());
    }

    @ParameterizedTest(name = "should throw exception for invalid length {0}")
    @ValueSource(ints = {-1, -5, Integer.MIN_VALUE})
    void createToken_InvalidLength_ThrowsException(int invalidLength) {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Token(10, invalidLength, (byte)'a')
        );
        assertEquals("Length cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("should handle zero values correctly")
    void createToken_ZeroValues_Success() {
        Token token = new Token(0, 0, (byte)0);

        assertAll(
                () -> assertEquals(0, token.offset()),
                () -> assertEquals(0, token.length()),
                () -> assertEquals(0, token.nextChar())
        );
    }

    @Test
    @DisplayName("should handle max values correctly")
    void createToken_MaxValues_Success() {
        Token token = new Token(Integer.MAX_VALUE, Integer.MAX_VALUE, Byte.MAX_VALUE);

        assertAll(
                () -> assertEquals(Integer.MAX_VALUE, token.offset()),
                () -> assertEquals(Integer.MAX_VALUE, token.length()),
                () -> assertEquals(Byte.MAX_VALUE, token.nextChar())
        );
    }

    @Test
    @DisplayName("should implement correct equals and hashCode")
    void equalsAndHashCode_ConsistentBehavior() {
        Token baseToken = new Token(10, 5, (byte)'a');
        Token sameToken = new Token(10, 5, (byte)'a');
        Token differentCharToken = new Token(10, 5, (byte)'b');
        Token differentOffsetToken = new Token(11, 5, (byte)'a');

        assertAll(
                () -> assertEquals(baseToken, sameToken),
                () -> assertEquals(baseToken.hashCode(), sameToken.hashCode()),
                () -> assertNotEquals(baseToken, differentCharToken),
                () -> assertNotEquals(baseToken, differentOffsetToken),
                () -> assertNotEquals(baseToken, null),
                () -> assertNotEquals(baseToken, new Object())
        );
    }

    @ParameterizedTest(name = "should generate correct string for token ({0},{1},{2})")
    @MethodSource("provideToStringTestCases")
    void toString_VariousTokens_CorrectFormat(int offset, int length, byte nextChar, String expected) {
        Token token = new Token(offset, length, nextChar);
        assertEquals(expected, token.toString());
    }

    @ParameterizedTest(name = "should handle special character {0}")
    @MethodSource("provideSpecialCharactersTestCases")
    void nextChar_SpecialCharacters_CorrectValue(byte nextChar, int expectedUnsigned) {
        Token token = new Token(1, 1, nextChar);
        assertEquals(expectedUnsigned, Byte.toUnsignedInt(token.nextChar()));
    }

    private static Stream<Arguments> provideToStringTestCases() {
        return Stream.of(
                arguments(3, 2, (byte)'a', "(3,2,a)"),
                arguments(0, 0, (byte)0, "(0,0,null)"),
                arguments(1, 1, (byte)'\n', "(1,1,\n)")
        );
    }

    private static Stream<Arguments> provideSpecialCharactersTestCases() {
        return Stream.of(
                arguments((byte)'\n', 10),
                arguments((byte)255, 255),
                arguments((byte)-128, 128)
        );
    }
}