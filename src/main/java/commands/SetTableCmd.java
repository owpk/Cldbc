package commands;

import core.Commands;
import sun.security.krb5.Config;
import util.ConfigParams;

public class SetTableCmd extends Commands implements Command {
    /**
     * change table name
     * from main menu str 'alias_name' 'dbName'
     * from CRUD menu str 'dbName'
     */

    private String alias;
    private String dBName;

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

    @Override
    public void execute() throws NullPointerException {
        ConfigParams cfg = null;
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
