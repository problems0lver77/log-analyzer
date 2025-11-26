# Java Log Analyzer

Многопоточный анализатор лог-файлов на Java, предназначенный для эффективной параллельной обработки.  
Считает количество ошибок и собирает статистику по IP-адресам с использованием современных средств Java Concurrency.

## Основные возможности

- **Многопоточность** через `ExecutorService` и `Callable`
- **Контроль параллелизма** с помощью `Semaphore`
- **Асинхронная обработка** с `CompletableFuture`
- **Потокобезопасное хранение результатов** через `ConcurrentHashMap` и `AtomicInteger`

## Пример использования

```java
List<String> files = List.of("logs/log1.txt", "logs/log2.txt", "logs/log3.txt");
LogAnalyzer analyzer = new LogAnalyzer(files, 3, 2);
ResultSummary result = analyzer.analyze();

System.out.println("Всего ошибок: " + result.getErrorCount());
result.getIpCounts().forEach((ip, count) -> System.out.println(ip + " -> " + count.get()));
