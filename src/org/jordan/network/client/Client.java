package org.jordan.network.client;

import org.jordan.network.ChatInteractor;

import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException {
        ChatInteractor interactor = new ChatInteractor(true, "Server: ");
        interactor.start();
    }
}