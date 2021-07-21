package main.hr.kcosic.project.controllers;

import main.hr.kcosic.project.models.DataWrapper;
import main.hr.kcosic.project.models.Player;
import main.hr.kcosic.project.models.enums.DataType;
import main.hr.kcosic.project.utils.LogUtils;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class GameServer {
    public static final int PORT_NUM = 5555;


    public static ServerThread getServerThread() {
        return serverThread;
    }

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

    private static class ServerThread extends Thread{
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

        private static ServerSocket serverSocket;

        @Override
        public void run() {
            LogUtils.logInfo("Running server");
            var isHost = true;

            try {
                serverSocket = new ServerSocket(PORT_NUM);//, 1, InetAddress.getByName("127.0.0.1"));
                LogUtils.logInfo("Server socket is ready");

            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                ClientHandler newClient = null;
                try {
                    LogUtils.logInfo("Server is listening");
                    var newClientSocket = serverSocket.accept();
                    if(clients.size() <= 4 || hasGameStarted){
                        newClient = new ClientHandler(newClientSocket, isHost);
                        if(isHost){
                            isHost = false;
                        }
                        LogUtils.logInfo("Server got new client");
                        clients.add(newClient);
                    }
                    else {
                        newClientSocket.close();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

        /**
         * Triggers start of the game
         * @param data
         */
        public static void startGame(DataWrapper data){
            for (var client : clients) {
                client.sendToClient(data);
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
         * Message is sent to all clients without regard of who sent it
         * @param data Data that is being sent to clients
         */
        public static void broadcast(DataWrapper data){
            for (var client : clients) {
                client.sendToClient(data);
            }
        }

        /**
         * Stops server thread, all client connections and closes server socket
         * @throws IOException
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
        private ObjectInputStream in;
        private Thread readThread;
        private Thread writeThread;
        private Player player;
        private final Socket clientSocket;


        public ClientHandler(Socket socket, boolean isHost) throws IOException {
            this.clientSocket = socket;
            this.isHost = isHost;
            setDaemon(true);
            this.start();

        }

        @Override
        public void run() {

            LogUtils.logInfo("New client is running");
            readThread = new Thread(() -> {
                LogUtils.logInfo("Reading");
                var exit = false;
                while(!exit){

                    try {
                        var data = (DataWrapper)(new ObjectInputStream(clientSocket.getInputStream())).readObject();
                        SortData(data);
                    }
                    catch (EOFException e) {
                        exit = true;

                    }
                    catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            });
            readThread.start();
        }

        /**
         * Parses data that client has sent and sorts it out for further crunch
         * @param data
         */
        private void SortData(DataWrapper data) {
            try{
                switch (data.getType()){
                    case MESSAGE, GAME_STATE, BOARD -> ServerThread.broadcast(data, this);
                    case PLAYER -> dealWithPlayer((Player)data.getData());
                    case START_GAME -> ServerThread.startGame(data);
                    default -> throw new InvalidClassException("Invalid class type. Has to be Player, String, Board or GameState");
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        /**
         * Deals with the player data that was received. Creates a new Player with same property values but assigns it a new ID and sends it to the host.
         * @param data
         */
        private void dealWithPlayer(Player data) {
            player = new Player(ServerThread.COUNT, data.getName(), data.getColor());
            ServerThread.COUNT++;
            ServerThread.sendToHost(new DataWrapper(DataType.PLAYER, player));
        }


        private void sendData(DataWrapper data) throws IOException {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(data);
            out.flush();
        }

        private void sendToClient(Object data, DataType type){
            writeThread = new Thread(() -> {
                var dataWrapper = new DataWrapper(type, data);
                try {
                    sendData(dataWrapper);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            writeThread.start();

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
            in.close();
            out.close();
            readThread.interrupt();
            writeThread.interrupt();
            clientSocket.close();
        }


    }
}
