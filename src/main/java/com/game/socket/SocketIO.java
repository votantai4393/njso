package com.game.socket;

import com.game.server.Config;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketIO {
    private static Socket socket;
    private static boolean isInitialized;
    private static boolean isConnected;

    public static void init() {
        if (isInitialized) {
            return;
        }
        reconnect(1);
        isInitialized = true;
    }

    public static void connect() {
        if (isConnected) {
            return;
        }
        try {
            Config config = Config.getInstance();
            socket = IO.socket(String.format("%s:%d", config.getWebsocketHost(), config.getWebsocketPort()));
            socket.connect();
            listen();
            isConnected = true;
        } catch (URISyntaxException e) {
            Logger.getLogger(SocketIO.class.getName()).log(Level.SEVERE, "Cannot connect to socket server", e);
            reconnect(10000);
        }
        Logger.getLogger(SocketIO.class.getName()).info("Connect to socket server successfully!");
    }

    public static void reconnect(long timeout) {
        new Thread(() -> {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                Logger.getLogger(SocketIO.class.getName()).log(Level.SEVERE, null, e);
            }
            connect();
        }).start();
    }

    public static void listen() {
        on(Action.NEW_TALENT_WAR, new NewTalentShowAction());
        on(Action.FORCE_OUT, new ForceOutAction());
        on(Action.EXCHANGE, new ExchangeAction());
    }

    public static void on(String event, IAction action) {
        socket.on(event, args -> {
            if (args.length == 0) {
                return;
            }
            JSONObject object = null;
            try {
                object = (JSONObject) args[0];
            } catch (Exception e) {
                Logger.getLogger(SocketIO.class.getName()).log(Level.SEVERE, null, e);
            }
            if (object == null) {
                return;
            }
            try {
                int serverId = object.getInt("server_id");
                if (serverId != -1 || serverId == Config.getInstance().getServerId()) {
                    action.call(object);
                }
            } catch (JSONException e) {
                Logger.getLogger(SocketIO.class.getName()).log(Level.SEVERE, null, e);
            }

        });
    }

    public static void on(byte event, IAction action) {
        on(String.valueOf(event), action);
    }

    public static void emit(String event, String data) {
        Object obj;
        try {
            obj = new JSONObject(data);
        } catch (JSONException e) {
            obj = data;
        }
        while (true) {
            try {
                JSONObject sender = new JSONObject();
                sender.put("data", obj);
                socket.emit(event, sender);
                break;
            } catch (JSONException ex) {
                Logger.getLogger(SocketIO.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException interruptedException) {
                    Logger.getLogger(SocketIO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void emit(byte event, String data) {
        emit(String.valueOf(event), data);
    }

    public static void close() {
        socket.disconnect();
    }
}
