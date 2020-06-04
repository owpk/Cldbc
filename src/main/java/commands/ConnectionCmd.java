package commands;

import commandListeners.MainClientListener;
import core.Client;
import core.BaseCommand;
import core.ConnectionManager;
import commandListeners.CrudCommandListener;

public class ConnectionCmd extends BaseCommand {
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
        if (notEmpty()) {
            Client.getClient().setCommandListener(
                    new CrudCommandListener(ConnectionManager.getManager().getConnectionList().get(alias))
            );
        } else {
            printThisCommandHelp();
        }
    }
}
