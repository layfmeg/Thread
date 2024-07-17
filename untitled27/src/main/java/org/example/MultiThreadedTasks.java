import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.*;
import java.util.stream.*;

public class MultiThreadedTasks {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        Callable<Void> task1 = () -> {
            int n1 = 0, n2 = 1;
            for (int i = 1; i < 15; i++) {
                int next = n1 + n2;
                n1 = n2;
                n2 = next;
            }
            writeToFile("фибоначчи: " + n2);
            return null;
        };

        Callable<Void> task2 = () -> {
            List<Integer> numbers = Files.lines(Paths.get("numbers.txt"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            int sum = numbers.stream().mapToInt(Integer::intValue).sum();
            writeToFile("сума: " + sum);
            return null;
        };

        Callable<Void> task3 = () -> {
            String content = new String(Files.readAllBytes(Paths.get("test.txt")));
            Matcher matcher = Pattern.compile("\\+\\d{12}").matcher(content);
            StringBuilder phoneNumbers = new StringBuilder("номера: ");
            while (matcher.find()) {
                phoneNumbers.append(matcher.group()).append(", ");
            }
            writeToFile(phoneNumbers.substring(0, phoneNumbers.length() - 2));
            return null;
        };

        try {
            List<Future<Void>> futures = executor.invokeAll(List.of(task1, task2, task3));
            for (Future<Void> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
            System.out.println("Все операции завершены");
        }
    }

    private static synchronized void writeToFile(String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("out.txt", true))) {
            writer.write(content);
            writer.newLine();
        }
    }
}
