package core;

import util.ConfigReader;

public class App {

    public static void main(String[] args) {
        ConfigReader.init();
        new ClientManager().commandListener();
    }
}


