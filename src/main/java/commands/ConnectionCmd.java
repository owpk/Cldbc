package commands;

import core.Client;
import core.Commands;
import core.ConnectionManager;
import core.CrudCommandListener;
import sun.misc.Cleaner;

import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionCmd extends Commands implements CommandInt {
    private String alias;

    public ConnectionCmd(String command) {
        super(command);
        cmd = CommandSet.CONNECT;
        alias = obtain(0);
    }

    @Override
    protected boolean notEmpty() {
        return alias != null && !alias.isEmpty();
    }


    @Override
    public void execute() {
        Client.setCommandListener(
        new CrudCommandListener(ConnectionManager.getManager().getConnectionList().get(alias))
        );
    }

    @Override
    public void handleException() {
        printThisCommandHelp();
        showAvailableAliasList();
    }
}
