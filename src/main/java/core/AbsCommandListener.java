package core;

import commands.*;
import java.util.Scanner;

public abstract class AbsCommandListener implements CommandListener {
    protected Scanner sc;

    public AbsCommandListener() {
        sc = new Scanner(System.in);
    }

    @Override
    public void close() {
        sc.close();
    }

    protected void commandService(CommandInt c) {
        try {
            c.execute();
        } catch (NullPointerException e) {
            System.out.println("wrong usage");
            c.handleException();
        }
    }

    @Override
    public void listenCommands(String command) {
        if (command.startsWith(CommandSet.ALIAS_PARAM.getCommandText()))
            commandService(new AliasParamCmd(command));
        else if (command.startsWith(CommandSet.CONFIG.getCommandText()))
            commandService(new ConfigCmd(command));
        else if (command.equals(CommandSet.ALIAS.getCommandText())) {
            Commands.showAvailableAliasList();
        } else if (command.equals(CommandSet.HELP.getCommandText()) || command.equals("?"))
            Commands.printHelp();
        else {
            System.out.println("Unknown command");
            System.out.println("If you want to init connection use - connect 'alias_name'");
            Commands.printHelp();
        }
    }
}
