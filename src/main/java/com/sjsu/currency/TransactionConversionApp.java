package com.sjsu.currency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TransactionConversionApp {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TransactionConversionApp.class, args);

        if (args.length != 2) {
            System.err.println("Usage: java -jar TransactionConversionApp.jar <input_file> <output_file>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        String fileExtension = getFileExtension(inputFile);

        FileFormatter fileFormatter = context.getBean(fileExtension, FileFormatter.class);
        fileFormatter.calculateConversion(inputFile, outputFile);
    }

    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }
        return fileName.substring(lastDotIndex + 1);
    }
}