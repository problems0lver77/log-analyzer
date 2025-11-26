package ru.zaikin;

import ru.zaikin.analyzer.LogAnalyzer;
import ru.zaikin.analyzer.ResultSummary;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<String> files = List.of(
                "logs/log1.txt",
                "logs/log2.txt",
                "logs/log3.txt"
        );

        LogAnalyzer analyzer = new LogAnalyzer(files, 3, 2);

        ResultSummary result = analyzer.analyze();

        System.out.println("Всего ошибок: " + result.getErrorCount());

        result.getIpCounts().forEach((ip, count) ->
                System.out.println(ip + " -> " + count.get())
        );
    }
}
