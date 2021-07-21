package main.hr.kcosic.project.utils;

import main.hr.kcosic.project.models.DataWrapper;

import java.io.*;
import java.net.Socket;

public class NetworkUtils {
    private static Socket socket = null;

    public static Socket connectToServer(String address, int port) throws IOException {
        socket = new Socket(address, port);
        socket.setKeepAlive(true);
        return socket;
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void disconnectFromServer() throws IOException {
        if(socket != null){
            socket.close();
        }
    }

    public static void sendData(DataWrapper data) throws IOException {
        var oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(data);
        oos.flush();
    }

}
