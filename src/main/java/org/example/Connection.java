package org.example;

class Connection {
    private static int counter = 0;
    private final int id;

    public Connection() {
        this.id = ++counter;
    }

    public void connect() {
        System.out.println("The " + Thread.currentThread().getName() + " - Connection for  " + id + " is now established.");
    }

    public void disconnect() {
        System.out.println("The " + Thread.currentThread().getName() + " - Connection for " + id + " is now closed.");
    }

    @Override
    public String toString() {
        return "Connection:id:" + id + ' ';
    }
}