package commandListeners;

import commands.*;

import java.util.Scanner;

public class MainClientListener extends BaseListener implements CommandListener {

    public boolean listenCommands(String command) {
        if (!super.listenCommands(command)) {
            if (command.startsWith(CommandSet.CONNECT.getCommandText())) {
                commandService(new ConnectionCmd(command));
                return true;
            } else if (command.startsWith(CommandSet.CONFIG.getCommandText())) {
                commandService(new ConfigCmd(command));
                return true;
            } else if (command.startsWith(CommandSet.SET_TABLE.getCommandText())) {
                commandService(new SetTableCmd(command));
                return true;
            } else {
                printUnknownCommandStack();
            }
        }
        return false;
    }

    @Override
    public void close() {
        sc.close();
    }

    public void setScanner(Scanner sc) {
        this.sc = sc;
    }


}
