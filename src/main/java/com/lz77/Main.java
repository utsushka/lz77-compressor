package com.lz77;

import com.lz77.algorithm.LZ77Compressor;
import com.lz77.algorithm.LZ77Decompressor;
import com.lz77.model.CompressionResult;
import com.lz77.model.Token;
import com.lz77.util.FileIO;
import com.lz77.util.Validation;

import java.util.List;
import java.util.Scanner;

// Главный класс приложения для сжатия/распаковки данных алгоритмом LZ77
public class Main {
    private static final LZ77Compressor compressor = new LZ77Compressor();
    private static final LZ77Decompressor decompressor = new LZ77Decompressor();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length > 0) {
            // Режим командной строки
            processCommandLine(args);
        } else {
            // Интерактивный режим
            runInteractiveMode();
        }
    }

    private static void processCommandLine(String[] args) {
        try {
            if (args[0].equals("compress") && args.length == 3) {
                compressFile(args[1], args[2]);
            } else if (args[0].equals("decompress") && args.length == 3) {
                decompressFile(args[1], args[2]);
            } else {
                printUsage();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            printUsage();
        }
    }

    private static void runInteractiveMode() {
        System.out.println("LZ77 Compression Tool");
        System.out.println("=====================");

        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Compress file");
            System.out.println("2. Decompress file");
            System.out.println("3. Exit");
            System.out.print("Select option: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        processCompression();
                        break;
                    case "2":
                        processDecompression();
                        break;
                    case "3":
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid option, try again.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private static void processCompression() {
        System.out.print("Enter input file path: ");
        String inputPath = scanner.nextLine();

        System.out.print("Enter output file path: ");
        String outputPath = scanner.nextLine();

        compressFile(inputPath, outputPath);
    }

    private static void processDecompression() {
        System.out.print("Enter compressed file path: ");
        String inputPath = scanner.nextLine();

        System.out.print("Enter output file path: ");
        String outputPath = scanner.nextLine();

        decompressFile(inputPath, outputPath);
    }

    public static void compressFile(String inputPath, String outputPath) {
        try {
            System.out.println("\nStarting compression...");
            byte[] inputData = FileIO.readFile(inputPath);

            // Добавьте лог для отладки
            System.out.println("Input size: " + inputData.length + " bytes");

            List<Token> tokens = compressor.compress(inputData);
            System.out.println("Tokens generated: " + tokens.size());

            FileIO.writeTokens(outputPath, tokens);

            // Проверка записи/чтения
            List<Token> testRead = FileIO.readTokens(outputPath);
            System.out.println("Tokens verified: " + (tokens.size() == testRead.size()));

            System.out.println("Compression completed successfully!");
        } catch (Exception e) {
            System.err.println("Detailed error:");
            e.printStackTrace(); // Вывод полного стека ошибки
            throw new RuntimeException("Compression failed: " + e.getMessage(), e);
        }
    }

    public static void decompressFile(String inputPath, String outputPath) {
        try {
            System.out.println("\nStarting decompression...");

            // Чтение токенов из файла
            List<Token> tokens = FileIO.readTokens(inputPath);

            // Распаковка данных
            byte[] outputData = decompressor.decompress(tokens);

            // Проверка целостности данных
            Validation.validateDecompressedData(tokens, outputData);

            // Сохранение распакованных данных
            FileIO.writeFile(outputPath, outputData);

            System.out.println("Decompression completed successfully!");
            System.out.printf("Original size: ~%d tokens%n", tokens.size());
            System.out.printf("Decompressed size: %d bytes%n", outputData.length);
            System.out.printf("Saved to: %s%n", outputPath);

        } catch (Exception e) {
            throw new RuntimeException("Decompression failed: " + e.getMessage(), e);
        }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  compress <input> <output>   - Compress input file");
        System.out.println("  decompress <input> <output> - Decompress input file");
    }
}