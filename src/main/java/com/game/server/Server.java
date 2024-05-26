package com.game.server;

public class Server {
    public static void main(String[] args) {
        System.out.println("hello world");
        Config config = Config.getInstance();
        boolean isLoad = config.load();
    }

}
