package com.game.server;

import lombok.Getter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class Config {
    private int port;
    private int serverId;
    private String websocketHost;
    private int websocketPort;
    private String mysqlHost;
    private int mysqlPort;
    private String mysqlUser;
    private String mysqlPass;
    private String mysqlDb;
    private String mongodbHost;
    private int mongodbPort;
    private String mongodbUser;
    private String mongodbPass;
    private String mongodbDb;

    @Getter
    private static final Config instance = new Config();

    public boolean load() {
        try (FileInputStream fileInputStream = new FileInputStream("config.properties")) {
            Properties properties = new Properties();
//            load properties
            properties.load(fileInputStream);
            port = Integer.parseInt(properties.getProperty("port"));
            serverId = Integer.parseInt(properties.getProperty("server.id"));
            websocketHost = properties.getProperty("websocket.host");
            websocketPort = Integer.parseInt(properties.getProperty("websocket.port"));
            mysqlHost = properties.getProperty("mysql.host");
            mysqlPort = Integer.parseInt(properties.getProperty("mysql.port"));
            mysqlUser = properties.getProperty("mysql.user");
            mysqlPass = properties.getProperty("mysql.pass");
            mysqlDb = properties.getProperty("mysql.db");
            mongodbHost = properties.getProperty("mongodb.host");
            mongodbPort = Integer.parseInt(properties.getProperty("mongodb.port"));
            mongodbUser = properties.getProperty("mongodb.user");
            mongodbPass = properties.getProperty("mongodb.pass");
            mongodbDb = properties.getProperty("mongodb.db");
//            log properties
            properties.forEach((key, value) -> System.out.println(key + " = " + value));
        } catch (IOException | NumberFormatException e) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    public String getJdbcUrl() {
        if (mysqlUser == null || mysqlPass == null) {
            return String.format("jdbc:mysql://%s:%d/%s",
                    mysqlHost, mysqlPort, mysqlDb);
        }
        return String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s",
                mysqlHost, mysqlPort, mysqlDb, mysqlUser, mysqlPass);
    }

    public String getMongodbUrl() {
        if (mongodbUser == null || mongodbPass == null) {
            return String.format("mongodb://%s:%d/%s",
                    mongodbHost, mongodbPort, mongodbDb);
        }
        return String.format("mongodb://%s:%d/%s?user=%s&password=%s",
                mongodbHost, mongodbPort, mongodbDb, mongodbUser, mongodbPass);
    }
}
