package core;

import util.ConfigReader;


public class App {
    public static void main(String[] args) {
        ConfigReader.init();
        Client client = Client.getClient();
        client.init();
        client.close();
    }
}
