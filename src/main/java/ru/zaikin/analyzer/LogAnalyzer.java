package ru.zaikin.analyzer;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class LogAnalyzer {

    private final List<String> logFiles;
    private final ExecutorService executor;
    private final Semaphore semaphore;

    public LogAnalyzer(List<String> logFiles, int maxThreads, int maxParallel) {
        this.logFiles = logFiles;
        this.executor = Executors.newFixedThreadPool(maxThreads);
        this.semaphore = new Semaphore(maxParallel);
    }

    public ResultSummary analyze() {
        /*
         *                    CompletableFuture vs Future:
         * - Future: базовый способ асинхронной задачи, чтобы получить результат нужно вызывать get(),
         *   что блокирует поток. Композиции задач почти нет, код получается громоздким.
         * - CompletableFuture: расширение Future с мощной функциональностью.
         *   Позволяет комбинировать задачи (thenApply, thenCompose, allOf), обрабатывать ошибки асинхронно
         *   и работать в потоковом/функциональном стиле.
         * - В этом проекте CompletableFuture.allOf() позволяет дождаться завершения всех задач сразу,
         *   а не блокировать поток на каждой задаче отдельно.
         * Итог: CompletableFuture удобнее и гибче для параллельной и асинхронной работы, чем простой Future.
         */

        List<CompletableFuture<ResultSummary>> futures = logFiles.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> {
                    try {
                        semaphore.acquire();
                        return new LogTask(file).call();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    } finally {
                        semaphore.release();
                    }
                }, executor))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        ResultSummary finalResult = new ResultSummary();
        for (CompletableFuture<ResultSummary> future : futures) {
            finalResult.merge(future.join());
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        return finalResult;
    }
}
