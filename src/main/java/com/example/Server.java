package com.example;

import com.example.controller.Controller;
import com.example.proxy.DynamicProxyHandler;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    public static void main(String[] args) {

        Server server = new Server();
        server.start();
    }

    public void start() {
        ServerSocket serverSocket = null;
        java.net.Socket socket = null;

        try {
            serverSocket = new ServerSocket(8888);
            System.out.println("server start!!");

            while (true) {
                socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                String data = null;
                StringBuilder headers = new StringBuilder();
                int contentLength = 0;
                String url = "";

                
                // 헤더 읽기               
                while (true) {

                    data = in.readLine();

                    if(data == null || data.isEmpty()) {
                        break;
                    }

                    //url 추출
                    if (headers.length() == 0) {
                        String[] requestLine = data.split(" ");
                        if (requestLine.length > 1) {
                            url = requestLine[1]; // 두 번째 요소가 URL 경로
                        }
                    }

                    if (data.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(data.split(" ")[1]);
                    }

                    headers.append(data);
                    headers.append("\r\n");
                }

                System.out.println(headers.toString());

                //body 읽기
                char[] body = new char[contentLength];
                in.read(body, 0, contentLength);
                String requestBody = new String(body);

                if ("/request".equals(url)) {
                    Controller proxyInstance = (Controller) Proxy.newProxyInstance(
                            Controller.class.getClassLoader(),
                            new Class[]{Controller.class},
                            new DynamicProxyHandler(requestBody)
                    );

                    System.out.println(proxyInstance.parseStringToJson(new MessageDto()));
                }


                out.write("HTTP/1.1 200 OK\n");
                out.write("Content-Type: application/json\n");
                out.write("\n");


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


//    public MessageDto parseStringToClass(String requestBody) throws Exception {
//        try {
//
//            Pattern pattern = Pattern.compile("\"([^\"]*)\"\\s*:\\s*\"([^\"]*)\"");
//            Matcher matcher = pattern.matcher(requestBody);
//
//            String property = null;
//            String value = null;
//
//            if (matcher.find()) {
//                property = matcher.group(1);
//                value = matcher.group(2);
//            }
//
//            if(property == null || value == null) {
//                return null;
//            }
//
//            Class<MessageDto> messageDtoClass = MessageDto.class;
//            Field field = messageDtoClass.getDeclaredField(property);
//
//            MessageDto instance = messageDtoClass.getDeclaredConstructor().newInstance();
//            field.set(instance, value);
//
//            return instance;
//        } catch (NoSuchFieldException e) {
//            return null;
//        }
//    }
}
