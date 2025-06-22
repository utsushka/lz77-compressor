package com.lz77.util;

import com.lz77.model.Token;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("File IO Utility Tests")
class FileIOTest {
    @TempDir
    Path tempDir;

    @Test
    @DisplayName("should read and write file content correctly")
    void readWriteFile_ContentPreserved() throws Exception {
        Path testFile = tempDir.resolve("test.txt");
        byte[] testData = "Hello, LZ77!".getBytes();

        FileIO.writeFile(testFile.toString(), testData);
        byte[] readData = FileIO.readFile(testFile.toString());

        assertArrayEquals(testData, readData, "Written and read data should match");
    }

    @ParameterizedTest(name = "should correctly serialize and deserialize tokens: {0}")
    @MethodSource("provideTokenTestCases")
    @DisplayName("should handle token serialization/deserialization")
    void readWriteTokens_VariousTokens_DataPreserved(String description, List<Token> tokens) throws Exception {
        Path tokensFile = tempDir.resolve("tokens.lz77");

        FileIO.writeTokens(tokensFile.toString(), tokens);
        List<Token> readTokens = FileIO.readTokens(tokensFile.toString());

        assertAll(
                () -> assertEquals(tokens.size(), readTokens.size(), "Token count should match"),
                () -> assertEquals(tokens, readTokens, "Tokens should be equal")
        );
    }

    @Test
    @DisplayName("should throw exception for invalid token file format")
    void readTokens_InvalidFile_ThrowsException() {
        Path invalidFile = tempDir.resolve("invalid.lz77");
        assertThrows(IOException.class,
                () -> FileIO.readTokens(invalidFile.toString()),
                "Should throw IOException for invalid format"
        );
    }

    @Test
    @DisplayName("should throw exception for non-existent file")
    void readFile_NonExistentFile_ThrowsException() {
        assertThrows(IOException.class,
                () -> FileIO.readFile("nonexistent.file"),
                "Should throw IOException for missing file"
        );
    }

    @ParameterizedTest(name = "should read text content: {0}")
    @MethodSource("provideTextContentTestCases")
    @DisplayName("should read text file content correctly")
    void readTextFile_VariousContent_ReturnsCorrectString(String content) throws Exception {
        Path textFile = tempDir.resolve("text.txt");
        FileIO.writeFile(textFile.toString(), content.getBytes());

        String readContent = FileIO.readTextFile(textFile.toString());
        assertEquals(content, readContent, "Read content should match original");
    }

    private static Stream<Arguments> provideTokenTestCases() {
        return Stream.of(
                arguments(
                        "Basic tokens",
                        List.of(
                                new Token(0, 0, (byte)'a'),
                                new Token(3, 2, (byte)'b'),
                                new Token(1, 4, (byte)0)
                        )
                ),
                arguments(
                        "Single token",
                        List.of(new Token(10, 5, (byte)'x'))
                ),
                arguments(
                        "Empty token list",
                        List.of()
                )
        );
    }

    private static Stream<Arguments> provideTextContentTestCases() {
        return Stream.of(
                arguments("Test content\nLine 2"),
                arguments("Single line"),
                arguments(""),  // Empty content
                arguments("Special chars: \t\n\r\u00A9")
        );
    }
}