package com.chatapp.peertopeerchatapp;

import java.io.*;
import java.net.*;

public class FileHandler {

    private final Socket socket;

   public FileHandler(Socket socket) { this.socket = socket; }

    private void sendFile(File file) {
        try (FileInputStream fileIn = new FileInputStream(file);
             OutputStream out = socket.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileIn.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void receiveFile(InputStream inputStream, File outputFile) {
        try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
