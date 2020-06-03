package commands;

import commandListeners.MainClientListener;
import core.Commands;
import core.ConnectionManager;

public class ConfigCmd extends Commands implements CommandInt {
    private String alias;
    {
        cmd = MainClientListener.CommandSet.CONFIG;
    }

    public ConfigCmd(String command) {
        super(command);
        alias = obtain(0);
    }

    public ConfigCmd(String command, String alias) {
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
