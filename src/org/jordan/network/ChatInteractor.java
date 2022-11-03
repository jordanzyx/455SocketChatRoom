package org.jordan.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ChatInteractor {
    // Interactor specific socket
    private Socket server;
    private Socket client;

    // Chat prefix
    private String otherPrefix;

    // Streams for sending and receiving data
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    // Thread / Runnable for Receiving chat
    private final Thread sendChat = new Thread(new Runnable() {
        @Override
        public void run() {
            Scanner console = new Scanner(System.in);
            while (true)
            {
                System.out.println(">> ");
                String message = console.nextLine();
                try {

                    if(message.startsWith("sendfile"))
                    {
                        // Get file name
                        String filename = message.split(" ")[1];

                        // Create stream for file
                        File file = new File(filename);
                        FileInputStream fileStream = new FileInputStream(file);

                        // Send header, file name, and length
                        outputStream.writeUTF("sendfile");
                        outputStream.writeUTF(filename);
                        outputStream.writeLong(file.length());

                        // Used to read file by 1024 bytes at a time
                        byte[] buffer = new byte[1024];
                        int bytes = 0;

                        // Write until file is copied over
                        while ((bytes=fileStream.read(buffer))!=-1){
                            outputStream.write(buffer,0,bytes);
                            outputStream.flush();
                        }

                        // Close file stream
                        fileStream.close();
                    }else outputStream.writeUTF(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private final Thread recieveChat = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true)
            {
                try {
                    String message = inputStream.readUTF();
                    if(message.startsWith("sendfile"))
                    {
                        String fileName = inputStream.readUTF();
                        long fileLength = inputStream.readLong();

                        // Open a new file
                        File file = new File(fileName);
                        FileOutputStream fileStream = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int bytes = 0;

                        // Loop until the we have read all the bytes
                        while (fileLength > 0 && (bytes = inputStream.read(buffer, 0, (int)Math.min(buffer.length, fileLength))) != -1) {
                            fileStream.write(buffer,0,bytes);
                            fileLength -= bytes;      // read upto file size
                        }

                        // Close down the file
                        fileStream.close();
                        System.out.println(otherPrefix + " Sent a file.");
                        System.out.println(">> ");
                    }else
                    {
                        System.out.println(otherPrefix + message);
                        System.out.println(">> ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private boolean isClient()
    {
        return server == null;
    }

    public ChatInteractor(boolean client, String prefix) throws IOException {
        if(client) {
            this.client = new Socket("localhost", 5000);
            this.inputStream = new DataInputStream(this.client.getInputStream());
            this.outputStream = new DataOutputStream(this.client.getOutputStream());
        }
        else {
            ServerSocket socket = new ServerSocket(5000);
            this.server = socket.accept();
            this.inputStream = new DataInputStream(this.server.getInputStream());
            this.outputStream = new DataOutputStream(this.server.getOutputStream());
        }
        this.otherPrefix = prefix;
    }

    public void start()
    {
        // Start the threads
        sendChat.start();
        recieveChat.start();

        while (true)
        {
            continue;
        }
    }
}