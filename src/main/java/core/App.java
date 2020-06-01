package core;

import util.ConfigReader;

public class App {
    //TODO refactor scanner (no constructor exchange)
    public static void main(String[] args) {
        ConfigReader.init();
        new ClientManager().commandListener();
    }
}


