package org.example;

class Connection {
    private static int counter = 0;
    private final int id;

    public Connection() {
        this.id = ++counter;
    }

    public void connect() {
        System.out.println(Thread.currentThread().getName() + " - Connection " + id + " established.");
    }

    public void disconnect() {
        System.out.println(Thread.currentThread().getName() + " - Connection " + id + " closed.");
    }

    @Override
    public String toString() {
        return "Connection{id=" + id + '}';
    }
}