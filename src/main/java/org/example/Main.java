package org.example;

import org.example.task1.RunnableEx;
import org.example.task1.ThreadEx;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {

//        //region task1
//        RunnableEx runnableTask = new RunnableEx();
//        Thread thread1 = new Thread(runnableTask);
//        thread1.start();
//
//        ThreadEx thread2 = new ThreadEx();
//        thread2.start();
//        //endregion task1
//
//        //region task2/3
//        ConnectionPool pool = ConnectionPool.getInstance(5);
//        ExecutorService executorService = Executors.newFixedThreadPool(7);
//
//
//        Runnable task = () -> {
//            Connection connection = pool.getConnection();
//            if (connection != null) {
//                try {
//                    connection.connect();
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                } finally {
//                    connection.disconnect();
//                    pool.releaseConnection(connection);
//                }
//            }
//        };
//
//
//        for (int i = 0; i < 7; i++) {
//            executorService.submit(task);
//        }
//
//        executorService.shutdown(); // Shutdown the executor
//        try {
//            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
//                executorService.shutdownNow();
//            }
//        } catch (InterruptedException e) {
//            executorService.shutdownNow();
//        }
//
//        System.out.println("all task are done!.");
        //endregion task2/3

        //region task4


        ConnectionPool pool = ConnectionPool.getInstance(5);


        ExecutorService executorService = Executors.newFixedThreadPool(7);

        Future<?>[] futures = new Future[5]; // Submit 5 tasks using Future interface
        for (int i = 0; i < 5; i++) {
            futures[i] = executorService.submit(() -> {
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
            });
        }


        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> { // Use CompletableFuture for the remaining 2 threads
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
        }, executorService);

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
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
        }, executorService);


        for (Future<?> future : futures) { // waiting Future tasks to complete
            try {
                future.get(); // this will block until all the task are completes
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Task interrupted or failed: " + e.getMessage());
            }
        }


        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2);
        combinedFuture.join();


        executorService.shutdown(); //shutdown
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        System.out.println("All the tasks are done!.");
        //endregion task4

    }
}
