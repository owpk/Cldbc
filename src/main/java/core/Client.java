package core;

import util.ConfigReader;

public class Client {

    public static void main(String[] args) {
        ConfigReader.init();
        new MainClientListener().commandListener();
    }
}


