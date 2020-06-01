package commands;

import core.Commands;
import core.ConnectionManager;

public class ConfigCmd extends Commands implements CommandInt {
    private String alias;

    public ConfigCmd(String command) {
        super(command);
        cmd = CommandSet.CONFIG;
        alias = obtain(0);
    }

    public ConfigCmd(String command, String alias) {
        cmd = CommandSet.CONFIG;
        this.alias = alias;
    }

    @Override
    protected boolean notEmpty() {
        return alias != null && !alias.isEmpty();
    }


    @Override
    public void execute() throws NullPointerException {
        System.out.println(
                ConnectionManager.getManager()
                        .getConnectionList()
                        .get(alias)
                        .getCfg()
                        .showParams());
    }

    @Override
    public void handleException() {
        printThisCommandHelp();
        showAvailableAliasList();
    }
}
