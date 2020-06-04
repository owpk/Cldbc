package core;

import commandListeners.CommandListener;
import commandListeners.MainClientListener;

import java.util.Scanner;

public class Client {
    private static Client client;
    private Scanner sc;
    private CommandListener commandListener;
    private CommandListener mainListener;

    public static Client getClient() {
        if (client == null) {
            MainClientListener mainClientListener = new MainClientListener();
            client = new Client(mainClientListener);
            mainClientListener.setScanner(client.sc);
            client.mainListener = mainClientListener;
            return client;
        }
        return client;
    }

    public Client(CommandListener commandListener) {
        this.commandListener = commandListener;
        sc = new Scanner(System.in);
    }

    public Scanner getSc() {
        return sc;
    }

    public void init() {
        while (!mainListener.isOver()) {
            System.out.print(client.commandListener.getName() + "> ");
            String command = sc.nextLine();
            command = command.toLowerCase().trim();
            if (!command.isEmpty())
                client.commandListener.listenCommands(command);
        }
    }

    public void close() {
        sc.close();
    }

    public CommandListener getMainListener() {
        return mainListener;
    }

    public CommandListener getCommandListener() {
        return commandListener;
    }

    public void setCommandListener(CommandListener commandListener) {
        this.commandListener = commandListener;
    }
}


