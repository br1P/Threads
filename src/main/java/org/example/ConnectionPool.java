package org.example;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

class ConnectionPool {
    private final BlockingQueue<Connection> connectionPool;
    private final int poolSize;
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);

    // Lazy-loaded singleton instance of the connection pool
    private static volatile ConnectionPool instance = null;

    // Private constructor to prevent direct instantiation
    private ConnectionPool(int size) {
        this.poolSize = size;
        this.connectionPool = new LinkedBlockingQueue<>(size);
    }

    // Singleton lazy initialization method
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

    // Lazy initialize connections only when needed
    private void initializeConnections() {
        if (isInitialized.compareAndSet(false, true)) {
            for (int i = 0; i < poolSize; i++) {
                connectionPool.offer(new Connection());
            }
            System.out.println("Connection pool initialized with " + poolSize + " connections.");
        }
    }

    // Get a connection from the pool
    public Connection getConnection() {
        initializeConnections(); // Ensure connections are initialized lazily
        Connection connection = null;
        try {
            connection = connectionPool.take(); // Get an available connection
            System.out.println(Thread.currentThread().getName() + " acquired " + connection);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Failed to get connection from pool: " + e.getMessage());
        }
        return connection;
    }

    // Return a connection to the pool
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                connectionPool.put(connection); // Put the connection back into the pool
                System.out.println(Thread.currentThread().getName() + " released " + connection);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Failed to release connection back to pool: " + e.getMessage());
            }
        }
    }
}

