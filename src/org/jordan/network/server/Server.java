package org.jordan.network.server;

import org.jordan.network.ChatInteractor;

import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException {
        ChatInteractor interactor = new ChatInteractor(false, "Client: ");
        interactor.start();
    }
}