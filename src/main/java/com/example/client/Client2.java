package com.example.client;

import java.io.*;
import java.net.*;

public class Client{
    public static void main(String[] args) {
        String serverHost = "localhost";
        int port = 42069;

        try (Socket socket = new Socket(serverHost, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connesso al server su " + serverHost + ":" + port);
            String userInputLine;

            while ((userInputLine = userInput.readLine()) != null) {
                out.println(userInputLine);
                System.out.println("Server: " + in.readLine());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
