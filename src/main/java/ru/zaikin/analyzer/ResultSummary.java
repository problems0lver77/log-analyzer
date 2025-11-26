package ru.zaikin.analyzer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ResultSummary {

    private final Map<String, AtomicInteger> ipCounts = new ConcurrentHashMap<>();
    private final AtomicInteger errorCount = new AtomicInteger();

    public void addIp(String ip) {
        ipCounts.computeIfAbsent(ip, k -> new AtomicInteger()).incrementAndGet();
    }

    public void addError() {
        errorCount.incrementAndGet();
    }

    public void merge(ResultSummary other) {
        other.getIpCounts().forEach((ip, count) ->
                ipCounts.computeIfAbsent(ip, k -> new AtomicInteger()).addAndGet(count.get())
        );
        errorCount.addAndGet(other.getErrorCount());
    }

    public Map<String, AtomicInteger> getIpCounts() {
        return ipCounts;
    }

    public int getErrorCount() {
        return errorCount.get();
    }
}
