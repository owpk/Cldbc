package core;

import commands.CommandSet;

import java.util.Scanner;

public class Client {
    private static CommandListener commandListener;
    private Scanner sc;

    public static void setCommandListener(CommandListener commandListener) {
        Client.commandListener = commandListener;
    }

    public Client(CommandListener commandListener) {
        Client.commandListener = commandListener;
        sc = new Scanner(System.in);
    }


    void listen(CommandListener c, String command) {
        c.listenCommands(command);
    }

    protected void switchCommandListener(CommandListener c) {
        c.close();
    }


    public void init() {
        while (true) {
            System.out.print("cldbc> ");
            String command = sc.nextLine();
            command = command.toLowerCase().trim();
            listen(commandListener, command);
            if (!command.isEmpty()) {
                if (command.equals(CommandSet.EXIT.getCommandText()))
                    break;
            }
        }
    }

    public void close() {
        sc.close();
    }
}


