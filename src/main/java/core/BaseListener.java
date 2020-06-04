package core;

import commandListeners.CommandListener;
import commandListeners.MainClientListener;
import commands.AliasParamCmd;
import commands.CommandInt;

import java.util.Scanner;

public abstract class BaseListener implements CommandListener {
    protected Scanner sc;
    protected String listenerName;
    protected boolean over;

    public BaseListener() {
        listenerName = "clbdc";
    }

    @Override
    public boolean listenCommands(String command) {
        if (command.startsWith(MainClientListener.CommandSet.ALIAS_PARAM.getCommandText())) {
            commandService(new AliasParamCmd(command));
            return true;
        } else if (command.equals(MainClientListener.CommandSet.ALIAS.getCommandText())) {
            BaseCommand.showAvailableAliasList();
            return true;
        } else if (command.equals(MainClientListener.CommandSet.HELP.getCommandText()) || command.equals("?")) {
            BaseCommand.printHelp();
            return true;
        } else if (command.equals(MainClientListener.CommandSet.EXIT.getCommandText())) {
            Client.getClient().getMainListener().setOver(true);
            close();
            return true;
        } else
            return false;
    }

    protected void printUnknownCommandStack() {
        System.out.println("Unknown command");
        System.out.println("If you want to init connection: " + MainClientListener.CommandSet.CONNECT.getCommandText() +
                MainClientListener.CommandSet.CONNECT.getCommandDescription());
        BaseCommand.printHelp();
    }

    protected void commandService(CommandInt c) {
        c.execute();
    }

    @Override
    public String getName() {
        return listenerName;
    }

    @Override
    public boolean isOver() {
        return over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }
}
