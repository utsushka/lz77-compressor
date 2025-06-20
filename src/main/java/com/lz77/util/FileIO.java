package com.lz77.util;

import com.lz77.model.Token;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для чтения/записи файлов и сериализации токенов
 */
public class FileIO {
    private static final String TOKENS_HEADER = "LZ77TOKENSv1.0";

    /**
     * Чтение файла в массив байтов
     */
    public static byte[] readFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }
        return Files.readAllBytes(path);
    }

    /**
     * Запись массива байтов в файл
     */
    public static void writeFile(String filePath, byte[] data) throws IOException {
        Path path = Path.of(filePath);
        Files.write(path, data);
    }

    public static void writeTokens(String filePath, List<Token> tokens) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath))) {
            dos.writeUTF(TOKENS_HEADER);
            dos.writeInt(tokens.size());

            for (Token token : tokens) {
                // Явно указываем типы при записи
                dos.writeInt(token.offset());    // Всегда 4 байта
                dos.writeInt(token.length());    // Всегда 4 байта
                dos.writeByte(token.nextChar()); // Всегда 1 байт
            }
        }
    }

    public static List<Token> readTokens(String filePath) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
            String header = dis.readUTF();
            if (!header.equals(TOKENS_HEADER)) {
                throw new IOException("Invalid file format");
            }

            int count = dis.readInt();
            List<Token> tokens = new ArrayList<>(count);

            for (int i = 0; i < count; i++) {
                // Чтение в том же порядке, что и запись
                int offset = dis.readInt();    // 4 байта
                int length = dis.readInt();    // 4 байта
                byte nextChar = dis.readByte(); // 1 байт
                tokens.add(new Token(offset, length, nextChar));
            }

            return tokens;
        }
    }

    /**
     * Чтение файла как текста (для тестов)
     */
    public static String readTextFile(String filePath) throws IOException {
        return new String(readFile(filePath));
    }
}