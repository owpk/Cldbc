package commandListeners;

import commands.AliasParamCmd;
import commands.CommandInt;
import commands.CommandSet;
import core.Commands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public abstract class BaseListener implements CommandListener {
    private static final Logger logger = LogManager.getLogger(MainClientListener.class.getName());
    protected Scanner sc;
    protected String listenerName;
    protected boolean over;

    public BaseListener() {
        listenerName = "clbdc";
    }

    @Override
    public boolean listenCommands(String command) {
        if (command.startsWith(CommandSet.ALIAS_PARAM.getCommandText())) {
            commandService(new AliasParamCmd(command));
            return true;
        } else if (command.equals(CommandSet.ALIAS.getCommandText())) {
            Commands.showAvailableAliasList();
            return true;
        } else if (command.equals(CommandSet.HELP.getCommandText()) || command.equals("?")) {
            Commands.printHelp();
            return true;
        } else if (command.equals(CommandSet.EXIT.getCommandText())) {
            over = true;
            close();
            return true;
        } else
            return false;
    }

    protected void printUnknownCommandStack() {
        System.out.println("Unknown command");
        System.out.println("If you want to init connection: " + CommandSet.CONNECT.getCommandText() +
                CommandSet.CONNECT.getCommandDescription());
        Commands.printHelp();
    }
    protected void commandService(CommandInt c) {
        try {
            c.execute();
        } catch (NullPointerException e) {
            System.out.println("wrong usage");
            c.handleException();
            logger.info(e.getMessage());
        }
    }

    @Override
    public String getName() {
        return listenerName;
    }

    @Override
    public boolean isOver() {
        return over;
    }

}
