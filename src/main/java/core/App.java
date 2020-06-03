package core;

import util.ConfigReader;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        ConfigReader.init();
        CommandListener commandListener = new MainClientListener();
        Client client = new Client(commandListener);
        client.init();
        client.close();
    }
}
