package commands;

import core.Commands;
import util.ConfigParams;

import java.util.Scanner;

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

    public SetTableCmd(String command, String alias,  Scanner sc) {
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
            dbConnection = CONNECTION_MANAGER.getConnectionList().get(alias);
            cfg = dbConnection.getCfg();
            cfg.setDbName(dBName);
            dbConnection.setCfg(cfg);
            System.out.println("DB name changed: " + dbConnection.getCfg().getDbName());
        }
    }

    @Override
    public void handleException() {
        printThisCommandHelp();
    }
}
