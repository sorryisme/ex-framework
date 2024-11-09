package com.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {

        Main main = new Main();
        main.socketServerStart();

    }

    public void socketServerStart() {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(8888);
            System.out.println("server start!!");

            while (true) {
                socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                String data = null;
                StringBuilder sb = new StringBuilder();

                while (true) {

                    data = in.readLine();

                    if( data == null || data.isEmpty()) {
                        break;
                    }

                    sb.append(data);
                    sb.append("\r\n");
                }

                System.out.println(sb.toString());
                out.write("HTTP/1.1 200 OK\n");
                out.write("Content-Type: text/plain\n");
                out.write("\n");
                out.write("Hello World!");

                out.close();
                in.close();
                inputStream.close();
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}