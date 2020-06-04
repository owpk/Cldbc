package commands;

import commandListeners.MainClientListener;
import core.BaseCommand;
import core.ConnectionManager;

public class ConfigCmd extends BaseCommand {
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
        if (notEmpty()) {
            System.out.println(
                    ConnectionManager.getManager()
                            .getConnectionList()
                            .get(alias)
                            .getCfg()
                            .showParams());
        } else {
            printThisCommandHelp();
            showAvailableAliasList();
        }
    }

}
