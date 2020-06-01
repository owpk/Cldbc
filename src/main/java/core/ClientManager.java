package core;

import commands.*;

import java.util.*;

public class ClientManager {
    protected static final Scanner sc = new Scanner(System.in);
    public static Scanner getScanner() {
        return sc;
    }

    private void commandService(Command c) {
        try {
            c.execute();
        } catch (NullPointerException | CmdException e) {
            System.out.println("wrong usage");
            c.handleException();
        }
    }

    public void commandListener() {
        while (true) {
            System.out.print("cldbc> ");
            String command = sc.nextLine();
            command = command.toLowerCase().trim();
            if (!command.isEmpty()) {
                if (command.equals(CommandSet.EXIT.getCommandText())) {
                    System.out.println("Exit");
                    break;
                } else if (command.startsWith(CommandSet.ALIAS_PARAM.getCommandText()))
                    commandService(new AliasParamCmd(command));
                else if (command.startsWith(CommandSet.CONFIG.getCommandText()))
                    commandService(new ConfigCmd(command));
                else if (command.equals(CommandSet.ALIAS.getCommandText())) {
                    Commands.showAvailableAliasList();
                } else if (command.equals(CommandSet.HELP.getCommandText()) || command.equals("?"))
                    Commands.printHelp();
                else if (command.startsWith(CommandSet.CONNECT.getCommandText()))
                    commandService(new ConnectionCmd(command));
                else if (command.startsWith(CommandSet.SET_TABLE.getCommandText()))
                    commandService(new SetTableCmd(command));
                else {
                    System.out.println("Unknown command");
                    System.out.println("If you want to init connection use - connect 'alias_name'");
                    Commands.printHelp();
                }
            }
        }
        closeResources();
    }

    private void closeResources() {
        sc.close();
    }

}
