package com.example.projectecripto;

import com.example.projectecripto.model.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private OnMessageReceived messageListener;

    public interface OnMessageReceived {
        void messageReceived(String message);
    }

    public SocketClient(String host, int port, OnMessageReceived messageListener) {
        this.messageListener = messageListener;
        try {
            socket = new Socket(host, port);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        if (printWriter != null) {
            printWriter.println(message.toJson());
        }
    }

    public void startListening(int currentId) {
        Thread thread = new Thread(() -> {
            String json;
            try {
                while ((json = bufferedReader.readLine()) != null) {
                    Message message = Message.fromJson(json);
                    if (message.getReceiverId()==currentId  && messageListener != null) {
                        messageListener.messageReceived(message.getContent());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void close() {
        if (printWriter != null) {
            printWriter.close();
        }
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
