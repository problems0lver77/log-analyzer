package ru.zaikin.analyzer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogTask implements Callable<ResultSummary> {

    private static final Pattern IP_PATTERN = Pattern.compile("^(\\d{1,3}(?:\\.\\d{1,3}){3})");

    private final String filePath;

    public LogTask(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public ResultSummary call() {
        ResultSummary summary = new ResultSummary();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("ERROR")) {
                    summary.addError();
                }

                String ip = extractIp(line);
                if (ip != null) {
                    summary.addIp(ip);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла " + filePath + ": " + e.getMessage());
        }

        return summary;
    }

    private String extractIp(String line) {
        if (line == null || line.isEmpty()) return null;
        Matcher matcher = IP_PATTERN.matcher(line);
        if (matcher.find()) {
            String ip = matcher.group(1);
            if (isValidIp(ip)) {
                return ip;
            }
        }
        return null;
    }

    private boolean isValidIp(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;
        for (String part : parts) {
            try {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
}
