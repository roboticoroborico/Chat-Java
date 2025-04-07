package com.example.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int PORT = 42069;
    private static Map<String, PrintWriter> clients = new HashMap<>();
    private static final String URL = "jdbc:postgresql://0.0.0.0:5432/chatdb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public static void main(String[] args) {
        connect();
        System.out.println("Acceso");
        
        try (ServerSocket serverSocket = new ServerSocket(PORT);){
            while (true) { 
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static Connection connect(){
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connessione riuscita!");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("Driver non trovato: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("❌ Errore di connessione: " + e.getMessage());
            return null;
        }
        return null;
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket){
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                //username check
                while (true) { 
                    username = in.readLine();
                    if (!clients.containsKey(username)){
                        out.println("ok");
                        break;
                    }
                    out.println("Username already taken");
                }

                //message on user connection
                synchronized (clients) {
                    clients.put(username, out);
                    // write connection message on the chat
                    System.out.println(">> Server: " + username + " connected to the server");
                    broadcastMessage(">> Server: " + username + " joined the chat");
                    // send to clients the name of the connected users
                    updateUserList();
                }

                // handling the message
                String message;
                while ((message = in.readLine()) != null){
                    System.out.println("Ricevuto: " + message);

                    if (message.startsWith("/")){
                        commandList(message.split("/")[1]);
                    }
                    else
                        broadcastMessage(username + ": " + message);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    socket.close();
                    clients.remove(username);
                    updateUserList();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    clients.remove(username);
                    updateUserList();
                }
            }
        }

        // private boolean isUsernameInvalid(String username) {
        //     boolean result = false;
        //     synchronized (clients) {
        //         if (username == null || username == "" || clients.containsKey(username)) {
        //             result = true;
        //             out.println("invalid");
        //             //out.println("You can't put an empty username");
        //         }
        //         else if (username.contains(" ")){
        //             String formattedName = username.replaceAll("\\s+", "");
        //             username = formattedName;
        //             System.out.println(username);
        //             //System.out.println(username);
        //             //result = true;
        //             //out.println("spazio");
        //         }

        //         // if (clients.containsKey(username)){
        //         //     result = true;
        //         //     //out.println("Username already taken");
        //         // }
        //     }
        //     return result;
        // }


        private void updateUserList(){
            String list = "/users " + String.join(", ", clients.keySet());
            broadcastMessage(list);
        }

        private void commandList(String command){
            String[] formattedMessage = command.split(" ", 2);
            switch (formattedMessage[0]) {
                case "listUsers" -> {
                    // Esegui la query e mostra gli utenti
                    getUsersFromDatabase();
                    out.println(">> Users listed from database.");
                }
                case "p" -> {
                    String[] privateMessage = formattedMessage[1].split(" ", 2);
                    privateMessage(privateMessage[0], username + ": (whisper) " + privateMessage[1]);
                }
                case "?" -> {
                    out.println("Commands available:");
                    out.println("/listUsers - Show users in the database");
                    out.println("/p <username> <message> - Private message");
                }
                default -> {
                    out.println(">> Server: comando non esistente");
                }
            }
        }
        

        private static void broadcastMessage(String message) {
            synchronized (clients) {
                for (PrintWriter printWriter : clients.values()) {
                    printWriter.println(message);
                }
            }
        }

        private static void getUsersFromDatabase() {
            try (Connection conn = connect(); 
                 java.sql.Statement stmt = conn.createStatement()) {
                String query = "SELECT username FROM users";
                java.sql.ResultSet rs = stmt.executeQuery(query);
                
                StringBuilder usersList = new StringBuilder("Users in DB: ");
                while (rs.next()) {
                    usersList.append(rs.getString("username")).append(", ");
                }
        
                // Rimuove l'ultima virgola e spazio
                if (usersList.length() > 0) {
                    usersList.setLength(usersList.length() - 2);
                }
        
                // Stampa nella console
                System.out.println(usersList.toString());
        
                // Invia la lista a tutti i client connessi
                broadcastMessage(usersList.toString());
        
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }        




        private void privateMessage(String user, String message){
            synchronized (clients) {
                if (clients.containsKey(user)) {
                    out.println(message + " ==> " + user);
                    clients.get(user).println(message);
                }
                else {
                    out.println("User doesn't exit");
                }
            }
        }
    }
}