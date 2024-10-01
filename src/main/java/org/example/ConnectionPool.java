package org.example;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

class ConnectionPool {
    private final BlockingQueue<Connection> connectionPool;
    private final int poolSize;
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);


    private static volatile ConnectionPool instance = null;               // Lazy-loaded singleton instance of the connection pool


    private ConnectionPool(int size) {
        this.poolSize = size;
        this.connectionPool = new LinkedBlockingQueue<>(size);
    }

    // lazy initialization methods
    public static ConnectionPool getInstance(int size) {
        if (instance == null) {
            synchronized (ConnectionPool.class) {
                if (instance == null) {
                    instance = new ConnectionPool(size);
                }
            }
        }
        return instance;
    }

    private void initializeConnections() {
        if (isInitialized.compareAndSet(false, true)) {
            for (int i = 0; i < poolSize; i++) {
                connectionPool.offer(new Connection());
            }
            System.out.println("Connection pool initialized with " + poolSize + " connections.");
        }
    }


    public Connection getConnection() {
        initializeConnections();
        Connection connection = null;
        try {
            connection = connectionPool.take();
            System.out.println(Thread.currentThread().getName() + " acquired " + connection);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Failed to get connection from pool: " + e.getMessage());
        }
        return connection;
    }


    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                connectionPool.put(connection);
                System.out.println(Thread.currentThread().getName() + " released " + connection);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Failed to release connection back to pool: " + e.getMessage());
            }
        }
    }
}

