package commands;

import commandListeners.MainClientListener;
import core.Client;
import core.ConnectionManager;
import commandListeners.CrudCommandListener;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionCmd extends BaseCommand {
    private String alias;

    {
        cmd = MainClientListener.CommandSet.CONNECT;
    }

    public ConnectionCmd(String command) {
        super(command);
        alias = obtain(0);
    }

    public ConnectionCmd(String command, String alias) {
        super(command);
        this.alias = alias;
    }

    @Override
    protected boolean notEmpty() {
        return alias != null && !alias.isEmpty();
    }


    @Override
    public void execute() {
        if (notEmpty()) {
            CrudCommandListener crudCommandListener = new CrudCommandListener(ConnectionManager.getManager().getConnectionList().get(alias));
            try {
                crudCommandListener.createConnection();
                Client.getClient().setCommandListener(crudCommandListener);
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println(e.getMessage());
                CrudCommandListener.getLogger().info(e.getStackTrace());
            }
        } else {
            printThisCommandHelp();
        }
    }
}
