package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import static java.lang.Thread.sleep;

public class Main {
    private static final String FILE_NAME = "./buf.txt";
    private static final int MAX_NUMBER = 930;
    private static final int ITERATIONS_LIMIT = 9500;
    private static final int PERIOD_TO_FINISH = 50;
    private static int generatorsCount;

    public static void main(String[] args) {
        try {
            Files.deleteIfExists(Paths.get(FILE_NAME));
        } catch (IOException e) {
            System.out.println(" 1 IOException: " + e.getMessage());
        }

        generatorsCount = 0;
        createGenNumberThread(false);
        createGenNumberThread(true);

        createReadThread();
    }
    private static void createGenNumberThread(boolean odd) {
        Runnable task = () -> {
            Random random = new Random();
            int add = odd ? 1 : 0;
            int middle = MAX_NUMBER / 2;
            try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
                for (int i = 0; i < ITERATIONS_LIMIT; i++) {
                    writer.write((2 * random.nextInt(middle) + add) + "\n");
                    writer.flush();
                }
                writer.close();
                sleep(PERIOD_TO_FINISH);
                decreaseCount();
            } catch (Exception e) {
                System.out.println(" 1 Exception: " + e.getMessage());
            }
        };

        Thread thread = new Thread(task);
        incrementCount();
        thread.start();
    }

    private static void createReadThread() {
        Runnable task = () -> {
            try {
                while (generationInProgress()) {
                    Files.lines(Path.of(FILE_NAME))
                            .forEach(System.out::println);
                }
            } catch (IOException e) {
                System.out.println(" IOException: " + e.getMessage());
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    private static void incrementCount() {
        generatorsCount++;
    }
    private static void decreaseCount() throws InterruptedException {
        generatorsCount--;
    }

    private static boolean generationInProgress() {
        return (generatorsCount > 0);
    }
}