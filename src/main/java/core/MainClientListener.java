package core;

import commands.*;

import java.util.*;

public class MainClientListener extends AbsCommandListener {

    public void listenCommands(String command) {
        super.listenCommands(command);
        if (command.startsWith(CommandSet.CONNECT.getCommandText())) {
            close();
            commandService(new ConnectionCmd(command));
        }else if (command.startsWith(CommandSet.SET_TABLE.getCommandText()))
            commandService(new SetTableCmd(command));
    }
}
