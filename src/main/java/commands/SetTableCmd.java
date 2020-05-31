package commands;

import core.Commands;

public class SetTableCmd extends Commands implements Command {
    private String alias;
    private String dBName;

    public SetTableCmd(String command) {
        super(command);
        dBName = obtain("dbname");
        cmd = CommandSet.SET_TABLE;
    }

    @Override
    protected void parseRowCommand() {

    }

    public SetTableCmd(String command, String alias) {
        super(command);
        this.alias = alias;
        dBName = obtain("dbname");
    }

    @Override
    public void execute() {
        connectionManager.getConnectionList().get(alias).getCfg().setDbName(dBName);
    }
}
