package core;

import commandListeners.CommandListener;
import commandListeners.MainClientListener;
import commands.CommandSet;

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
            client.setMain(mainClientListener);
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

    void listen(CommandListener c, String command) {
        c.listenCommands(command);
    }

    public void init() {
        while (true) {
            System.out.print("cldbc> ");
            String command = sc.nextLine();
            command = command.toLowerCase().trim();
            if (!command.isEmpty()) {
                listen(Client.getClient().getCommandListener(), command);
                if (command.equals(CommandSet.EXIT.getCommandText()))
                    break;
            }
        }
    }

    public void close() {
        sc.close();
    }

    public void setMain(CommandListener commandListener) {
        this.mainListener = commandListener;
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


