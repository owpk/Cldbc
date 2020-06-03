package commandListeners;

import commands.*;
import core.AbsCommandListener;
import core.Commands;

import java.util.*;

public class MainClientListener extends AbsCommandListener {

    public boolean listenCommands(String command) {
        if (!super.listenCommands(command)) {
            if (command.startsWith(CommandSet.CONNECT.getCommandText())) {
                commandService(new ConnectionCmd(command));
                return true;
            } else if (command.startsWith(CommandSet.SET_TABLE.getCommandText())) {
                commandService(new SetTableCmd(command));
                return true;
            } else {
                System.out.println("Unknown command");
                System.out.println("If you want to init connection use - connect 'alias_name'");
                Commands.printHelp();
                return false;
            }
        }
        return false;
    }
}
