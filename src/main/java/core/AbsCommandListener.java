package core;

import commandListeners.CommandListener;
import commands.*;

import java.util.Scanner;

public abstract class AbsCommandListener implements CommandListener {
    protected Scanner sc;

    public AbsCommandListener() {
    }


    protected void commandService(CommandInt c) {
        try {
            c.execute();
        } catch (NullPointerException e) {
//            System.out.println("wrong usage");
//            c.handleException();
            e.printStackTrace();
        }
    }

    @Override
    public boolean listenCommands(String command) {
        sc = Client.getClient().getSc();
        if (command.startsWith(CommandSet.ALIAS_PARAM.getCommandText())) {
            commandService(new AliasParamCmd(command));
            return true;
        } else if (command.startsWith(CommandSet.CONFIG.getCommandText())) {
            commandService(new ConfigCmd(command));
            return true;
        } else if (command.equals(CommandSet.ALIAS.getCommandText())) {
            Commands.showAvailableAliasList();
            return true;
        } else if (command.equals(CommandSet.HELP.getCommandText()) || command.equals("?")) {
            Commands.printHelp();
            return true;
        }
        return false;
    }
}
