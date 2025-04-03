package com.example.old;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 42069;

    private PrintWriter out;
    private BufferedReader in;

    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JList<String> userList;
    private DefaultListModel userListModel;
    private JButton darkModeButton;
    private JOptionPane alertWindow = new JOptionPane();

    private String username;

    public Client() {
        frame = new JFrame("Spaiciat");
        chatArea = new JTextArea(20, 40);
        chatArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        chatArea.setEditable(false);
        chatArea.setBackground(Color.WHITE);
        messageField = new JTextField( "Enter message", 50);
        messageField.setFont(new Font("Consolas", Font.PLAIN, 15));
        messageField.setForeground(Color.GRAY);
        sendButton = new JButton("Send");
        darkModeButton = new JButton("Swith mode");

        JPanel panel = new JPanel();
        panel.add(messageField);
        panel.add(sendButton);
        panel.add(darkModeButton);

        panel.setBackground(Color.LIGHT_GRAY);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);

        JPanel userListPanel = new JPanel();
        userListPanel.add(userList);
        userListPanel.setPreferredSize(new Dimension(225, 0));
        userListPanel.setBackground(Color.LIGHT_GRAY);

        userListPanel.setFont(new Font("Consolas", Font.PLAIN, 12));

        frame.getContentPane().add(chatArea, BorderLayout.CENTER);
        frame.getContentPane().add(userListPanel, BorderLayout.EAST);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        connectToServer();

        messageField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (messageField.getForeground() == Color.GRAY) {
                    messageField.setForeground(Color.BLACK);
                    messageField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (messageField.getText().equals("")){
                    messageField.setForeground(Color.GRAY);
                    messageField.setText("Enter message");
                }
            }
        });

        sendButton.addActionListener(event -> sendMessage());
        messageField.addActionListener(event -> sendMessage());
        darkModeButton.addActionListener(event -> {
            if(chatArea.getBackground() == Color.WHITE){
                chatArea.setBackground(Color.BLACK);
                chatArea.setForeground(Color.WHITE);
                userListPanel.setBackground(Color.DARK_GRAY);
                userListPanel.setForeground(Color.WHITE);
                panel.setBackground(Color.GRAY);
            }
            else {
                chatArea.setBackground(Color.WHITE);
                chatArea.setForeground(Color.BLACK);
                userListPanel.setBackground(Color.LIGHT_GRAY);
                userListPanel.setForeground(Color.BLACK);
                panel.setBackground(Color.LIGHT_GRAY);
            }

        });
    }

    private void sendMessage(){
        String message = messageField.getText().trim();
        if (!message.isEmpty() && !(messageField.getForeground() == Color.GRAY)){
            out.println(message);
            messageField.setText("");
        }
    }

    private void connectToServer(){
        try {
            Socket socket = new Socket(SERVER_IP, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String msg = "";

            do {
                username = alertWindow.showInputDialog(frame, !msg.isEmpty() ? msg : "Enter username: ");
                if (username == null){
                    System.exit(0);
                }
                else if(!controlUsername()){
                    out.println(username);
                    msg = in.readLine();
                }
            } while (!msg.equals("ok"));

            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null){
                        if (serverMessage.startsWith("/users ")){
                            updateUserList(serverMessage.replace("/users ", ""));
                        }
                        else {
                            chatArea.append(serverMessage + "\n");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean controlUsername() {
        boolean result = false;
        if (username.equals("")) {
            result = true;
            alertWindow.showMessageDialog(frame, "You can't write an empty username", "Error", 0);
        }
        else if (username.contains(" ")){
            username = username.replaceAll("\\s+", "");
        }

        return result;
    }

    private void updateUserList(String message){
        userListModel.clear();
        for (String username : message.split(", ")) {
            userListModel.addElement(username);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }
}
