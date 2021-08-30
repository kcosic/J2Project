package main.hr.kcosic.project.controllers;

import javafx.scene.paint.Color;
import main.hr.kcosic.project.models.ChatService;
import main.hr.kcosic.project.models.DataWrapper;
import main.hr.kcosic.project.models.Player;
import main.hr.kcosic.project.models.enums.DataType;
import main.hr.kcosic.project.models.enums.SvgEnum;
import main.hr.kcosic.project.utils.LogUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GameServer {
    public static final int PORT_NUM = 5555;

    private static ServerSocket serverSocket;
    private static ServerThread serverThread = null;

    public static void start(){
        if(serverThread == null){
            serverThread = new ServerThread();
        }
        serverThread.start();
        LogUtils.logInfo("Started a new thread");
    }

    public static void stop() throws IOException {
        serverThread.stopServerThread();
    }

    private static class ServerThread extends Thread {
        /**
         * List of connected clients
         */
        private static final List<ClientHandler> clients = new ArrayList<>();
        /**
         * Counts how many connections server has so it knows in which order they came
         */
        private static int COUNT = 1;
        /**
         * Used for knowing if the game has started already
         */
        private static boolean hasGameStarted = false;



        private static final String CLIENT_NAME = "Client";
        private static final String RMI_CLIENT = "client";
        private static final String RMI_SERVER = "server";
        private static final int REMOTE_PORT = 5001;
        private static final int RANDOM_PORT_HINT = 0;


        // we must keep a strong reference to service object, to avoid gc!
        private static ChatService chatServer;
        private static Registry registry;


        @Override
        public void run() {
            LogUtils.logInfo("Running server");
            var isHost = true;
            publishChatServer();
            try {
                serverSocket = new ServerSocket(PORT_NUM);//, 1, InetAddress.getByName("127.0.0.1"));
                LogUtils.logInfo("Server socket is ready");

            } catch (IOException e) {
                e.printStackTrace();
            }
            var exit = false;
            while (!exit) {
                ClientHandler newClient;
                try {
                    if(this.isInterrupted()){
                        exit = true;
                    }
                    else {
                        LogUtils.logInfo("Server is listening");
                        var newClientSocket = serverSocket.accept();
                        if (clients.size() <= 4 || hasGameStarted) {
                            newClient = new ClientHandler(newClientSocket, isHost);
                            if (isHost) {
                                isHost = false;
                            }
                            LogUtils.logInfo("Server got new client");
                            clients.add(newClient);
                        } else {
                            newClientSocket.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

        private static void publishChatServer() {
            chatServer = data -> clients.forEach(clientHandler -> {
                LogUtils.logInfo("Sending message to all but sender->wanted client: " + data.getSenderId() + "| current client: " + clientHandler.getPlayerId());
                if(clientHandler.getPlayerId() == data.getSenderId() ){
                    LogUtils.logInfo("Sending message to all but sender");

                    broadcast(data, clientHandler);
                }
            });
            // publish server
            try {
                registry = LocateRegistry.createRegistry(REMOTE_PORT);
                ChatService stub = (ChatService) UnicastRemoteObject.exportObject(chatServer, RANDOM_PORT_HINT);
                registry.rebind(RMI_SERVER, stub);
            } catch (RemoteException ex) {
                Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /**
         * Triggers start of the game
         */
        public static void startGame(){
            for (var client : clients) {
                client.sendToClient(new DataWrapper(DataType.START_GAME, client.player));
            }
            hasGameStarted = true;
        }

        /**
         * Sends player object to the host
         * @param data Data that is being sent
         */
        public static void sendToHost(DataWrapper data){
            for (var client : clients) {
                if (client.isHost) {
                    client.sendToClient(data);
                    break;
                }
            }
        }

        /**
         * Broadcasts data to all clients but sender
         * @param data Data that is being sent to clients
         * @param currentClient client that has sent the message
         */
        public static void broadcast(DataWrapper data, ClientHandler currentClient){
            for (var client : clients) {
                if(!client.equals(currentClient))
                {
                    client.sendToClient(data);
                }
            }
        }

        /**
         * Stops server thread, all client connections and closes server socket
         * @throws IOException Is thrown if there is an error while trying to stop client connection
         */
        public void stopServerThread() throws IOException {
            for (var client : clients) {
                client.stopConnection();
            }
            serverSocket.close();
            interrupt();
        }
    }

    /**
     * Acts as a client in eyes of the server and runs in its own thread.
     */
    protected static class ClientHandler extends Thread {
        /**
         * Checks to see if this client is the one that hosted the server
         */
        public final boolean isHost;
        private ObjectOutputStream out;
        private Thread readThread;
        private Thread writeThread;
        private Player player;
        private final Socket clientSocket;

        public int getPlayerId(){
            if(player == null){
                return -1;
            }
            return player.getId();
        }
        public ClientHandler(Socket socket, boolean isHost) {
            this.clientSocket = socket;
            this.isHost = isHost;
            setDaemon(true);
            start();
        }

        @Override
        public void run() {

            LogUtils.logInfo("New client is running");
            readThread = new Thread(() -> {
                LogUtils.logInfo("Reading");
                var exit = false;
                while(!exit){

                    try {
                        if(readThread.isInterrupted()){
                            exit = true;
                        }
                        else {
                            var data = (DataWrapper) (new ObjectInputStream(clientSocket.getInputStream())).readObject();
                            SortData(data);
                        }
                    }catch(SocketException | EOFException e){
                        exit = true;
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            });
            readThread.start();
        }

        /**
         * Parses data that client has sent and sorts it out for further crunch
         * @param data DataWrapper object that is received from the client
         */
        private void SortData(DataWrapper data) {
            try{
                switch (data.getType()){
                    case MESSAGE -> ServerThread.chatServer.send(data);
                    case GAME_STATE, BOARD, END_GAME -> ServerThread.broadcast(data, this);
                    case PLAYER -> dealWithPlayer((Player)data.getData());
                    case START_GAME -> ServerThread.startGame();
                    case DISCONNECT -> handleDisconnect(data);
                    default -> throw new InvalidClassException("Invalid class type. Has to be Player, String, Board or GameState");
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        private void handleDisconnect(DataWrapper data) {
            ServerThread.sendToHost(data);
            ServerThread.clients.remove(this);
            try {
                stopConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Deals with the player data that was received. Creates a new Player with same property values but assigns it a new ID and sends it to the host.
         * @param data Player data
         */
        private void dealWithPlayer(Player data) {
            player = new Player(ServerThread.COUNT - 1, data.getName(), getPlayerColor(), getPlayerPawn());
            ServerThread.COUNT++;
            ServerThread.sendToHost(new DataWrapper(DataType.PLAYER, player));
        }

        private SvgEnum getPlayerPawn() {
            return switch (ServerThread.clients.size()) {
                case 0 -> SvgEnum.PAWN_BLUE;
                case 1 -> SvgEnum.PAWN_RED;
                case 2 -> SvgEnum.PAWN_GREEN;
                case 3 -> SvgEnum.PAWN_YELLOW;
                default -> null;
            };
        }

        private String getPlayerColor() {
            return switch (ServerThread.clients.size()) {
                case 0 -> "blue";
                case 1 -> "red";
                case 2 -> "green";
                case 3 -> "#b7b219";
                default -> null;
            };
        }


        private void sendData(DataWrapper data) throws IOException {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(data);
            out.flush();
        }

        private void sendToClient(DataWrapper data){
            writeThread = new Thread(() -> {
                try {
                    sendData(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writeThread.start();

        }

        public void stopConnection() throws IOException {
            if(out != null){
                out.close();
            }
            if(readThread != null){
                readThread.interrupt();
            }
            if(writeThread != null){
                writeThread.interrupt();
            }
            clientSocket.close();
        }


    }
}
