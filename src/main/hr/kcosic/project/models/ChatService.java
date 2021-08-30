package main.hr.kcosic.project.models;

import javafx.scene.paint.Color;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatService extends Remote {
    void send(DataWrapper message) throws RemoteException;
}
