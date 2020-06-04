package commands;

import commandListeners.MainClientListener;
import connection.DBConnection;
import core.BaseCommand;
import core.ConnectionManager;

public class SetTableCmd extends BaseCommand {
    /**
     * change table name
     * from main menu set 'alias_name' 'dbName'
     * from CRUD menu set 'dbName'
     */

    private String alias;
    private String dBName;

    public SetTableCmd(String command) {
        super(command);
        alias = obtain(0);
        dBName = obtain(1);
        cmd = MainClientListener.CommandSet.SET_TABLE;
    }

    public SetTableCmd(String command, String alias) {
        super(command);
        this.alias = alias;
        dBName = obtain(0);
    }

    protected boolean notEmpty() {
        return alias != null && !alias.isEmpty() && dBName != null && !dBName.isEmpty();
    }

    @Override
    public void execute() throws NullPointerException {
        if (notEmpty()) {
            DBConnection dbConnection = ConnectionManager.getManager().getConnectionList().get(alias);
            dbConnection.getCfg().setDbName(dBName);
            System.out.println("DB name changed: " + dbConnection.getCfg().getDbName());
        } else {
            printThisCommandHelp();
        }
    }
}
