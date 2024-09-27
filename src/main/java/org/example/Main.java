package org.example;

import org.example.task1.RunnableEx;
import org.example.task1.ThreadEx;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
/*
        //region task1
        RunnableEx runnableTask = new RunnableEx();
        Thread thread1 = new Thread(runnableTask);
        thread1.start();

        ThreadEx thread2 = new ThreadEx();
        thread2.start();
        //endregion task1

        //region task2/3
        // Create a connection pool of size 5
        ConnectionPool pool = ConnectionPool.getInstance(5);

        // Create an ExecutorService with 7 threads
        ExecutorService executorService = Executors.newFixedThreadPool(7);

        // Runnable task to get a connection, simulate some work, and release it
        Runnable task = () -> {
            Connection connection = pool.getConnection();
            if (connection != null) {
                try {
                    connection.connect();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    connection.disconnect();
                    pool.releaseConnection(connection);
                }
            }
        };


        for (int i = 0; i < 7; i++) { // Submit 7 tasks to the executor service
            executorService.submit(task);
        }

        executorService.shutdown(); // Shutdown the executor
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        System.out.println("All tasks have been completed.");
        //endregion task2/3
        */
        //region task4


        ConnectionPool pool = ConnectionPool.getInstance(5);


        ExecutorService executorService = Executors.newFixedThreadPool(7);

        // Submit 5 tasks using Future interface to acquire and release connections
        Future<?>[] futures = new Future[5];
        for (int i = 0; i < 5; i++) {
            futures[i] = executorService.submit(() -> {
                Connection connection = pool.getConnection();
                if (connection != null) {
                    try {
                        connection.connect();  // Mock connection usage
                        Thread.sleep(2000);    // Simulate some work with the connection
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        connection.disconnect();
                        pool.releaseConnection(connection);
                    }
                }
            });
        }

        // Use CompletableFuture for the remaining 2 threads that should wait for a connection
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            Connection connection = pool.getConnection();
            if (connection != null) {
                try {
                    connection.connect();  // Mock connection usage
                    Thread.sleep(2000);    // Simulate some work with the connection
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    connection.disconnect();
                    pool.releaseConnection(connection);
                }
            }
        }, executorService);

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            Connection connection = pool.getConnection();
            if (connection != null) {
                try {
                    connection.connect();  // Mock connection usage
                    Thread.sleep(2000);    // Simulate some work with the connection
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    connection.disconnect();
                    pool.releaseConnection(connection);
                }
            }
        }, executorService);

        // Wait for all Future tasks to complete
        for (Future<?> future : futures) {
            try {
                future.get(); // Blocks until the task completes
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Task interrupted or failed: " + e.getMessage());
            }
        }

        // Wait for CompletableFuture tasks to complete
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2);
        combinedFuture.join(); // Wait for both CompletableFutures to finish

        // Shutdown the executor service gracefully
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        System.out.println("All tasks have been completed.");
    }
        //endregion task4

    }
