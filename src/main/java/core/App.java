package core;

public class App {

    public static void main(String[] args) {
        ConfigReader.init();
        new ClientManager().commandListener();
    }
}


