package com.example.projectecripto;

import android.util.Log;

import com.example.projectecripto.model.Contact;
import com.example.projectecripto.model.Message;
import com.example.projectecripto.model.SocketMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Arrays;

public class SocketClient {
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private OnMessageReceived messageListener;
    private String host;
    private int port;

    public interface OnMessageReceived {
        void messageReceived(String message);
    }

    public SocketClient(String host, int port, OnMessageReceived messageListener) {
        Log.v("SocketClient", "Connecting to " + host + ":" + port);
        this.messageListener = messageListener;
        this.host = host;
        this.port = port;
        try {
            socket = new Socket(host, port);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            startListening();
            Log.v("SocketClient", "Connected to " + host + ":" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Object object) {
        Thread thread = new Thread(() -> {
            if (printWriter != null) {
                SocketMessage socketMessage;
                if(object instanceof Message){
                    Message message = (Message) object;
                    socketMessage = new SocketMessage("message", message.toJson(), "",message.getContactId(), message.getReceiverId());
                    Log.v("SocketClient", "Sending message: " + message.toJson());
                } else if (object instanceof Contact) {
                    Contact contact = (Contact) object;
                    socketMessage = new SocketMessage("credentials", contact.toJson(),"simetricKey", contact.getId(), 0);
                    Log.v("SocketClient", "Sending credentials: " + contact.toJson());
                } else if(object instanceof PublicKey){
                    PublicKey publicKey = (PublicKey) object;
                    Log.v("SocketClient", "Sending public key");
                    Log.v("SocketClient", "Sending public key: " + Xifrador.publicKeyToString(publicKey));
                    printWriter.println(Xifrador.publicKeyToString(publicKey));
                    return;
                }else{
                    return;
                }
                try {
                    Log.v("SocketClient", "Sending message: " + socketMessage.toJson());
                    objectOutputStream.writeObject(Xifrador.encryptWrappedData(socketMessage.toJson()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();

    }

    public void startListening() {
        sendMessage(Xifrador.getPublicKey());
        Thread thread = new Thread(() -> {
            byte[][] receivedData;
            try {
                Xifrador.setServerPublicKey(Xifrador.getPublicKeyFromString(bufferedReader.readLine()));
                sendMessage(Contact.getCurrentContact());
                Log.v("SocketClient", "Received public key");
                while (!socket.isClosed() & (receivedData = (byte[][]) objectInputStream.readObject()) != null) {
                    Log.v("SocketClient", "Received message: " + Arrays.deepToString(receivedData));
                    messageListener.messageReceived(Xifrador.decryptWrappedData(receivedData));
                }
            } catch (IOException e) {
                if (e instanceof java.net.SocketException) {
                    e.printStackTrace();
                    Log.v("SocketClient", "Connection lost. Attempting to reconnect...");
                    this.close();
                    try {
                        this.socket = new Socket(host, port);
                        this.printWriter = new PrintWriter(socket.getOutputStream(), true);
                        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        this.startListening();
                    }catch (IOException ex){
                        ex.printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                }
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void close() {
        Log.v("SocketClient", "Closing connection");
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
