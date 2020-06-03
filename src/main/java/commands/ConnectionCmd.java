package commands;

import commandListeners.MainClientListener;
import core.Client;
import core.Commands;
import core.ConnectionManager;
import commandListeners.CrudCommandListener;

public class ConnectionCmd extends Commands implements CommandInt {
    private String alias;

    public ConnectionCmd(String command) {
        super(command);
        cmd = MainClientListener.CommandSet.CONNECT;
        alias = obtain(0);
    }

    @Override
    protected boolean notEmpty() {
        return alias != null && !alias.isEmpty();
    }


    @Override
    public void execute() {
        Client.getClient().setCommandListener(
                new CrudCommandListener(ConnectionManager.getManager().getConnectionList().get(alias))
        );
    }

    @Override
    public void handleException() {
        printThisCommandHelp();
        showAvailableAliasList();
    }
}
