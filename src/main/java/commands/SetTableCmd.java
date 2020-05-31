package commands;

import connection.DBConnection;
import core.Commands;
import sun.security.krb5.Config;
import util.ConfigParams;

import java.sql.SQLException;

public class SetTableCmd extends Commands implements Command {
    /**
     * change table name
     * from main menu str 'alias_name' 'dbName'
     * from CRUD menu str 'dbName'
     */

    private String alias;
    private String dBName;
    private ConfigParams cfg;

    public SetTableCmd(String command) {
        super(command);
        alias = obtain(0);
        dBName = obtain(1);
        cmd = CommandSet.SET_TABLE;
    }

    public SetTableCmd(String command, String alias) {
        super(command);
        this.alias = alias;
        dBName = obtain(0);
    }

    protected boolean notEmpty() {
        return alias != null && !alias.isEmpty() && dBName != null && !dBName.isEmpty();
    }

    public void reconnect() {
        try {
            connection = new DBConnection(cfg).createConn();
        } catch (SQLException | ClassNotFoundException s) {
            System.out.println(s.getMessage());
        }
    }

    @Override
    public void execute() throws NullPointerException {
        if (notEmpty())
        cfg = connectionManager.getConnectionList().get(alias).getCfg();
        cfg.setDbName(dBName);
        System.out.println("DB name changed: " + cfg.getDbName());
    }

    @Override
    public void handleException() {
        printThisCommandHelp();
    }
}
