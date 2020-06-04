package commands;

import commandListeners.MainClientListener;
import core.BaseCommand;
import core.ConnectionManager;

public class AliasParamCmd extends BaseCommand {
    private String alias;

    public AliasParamCmd(String command) {
        super(command);
        cmd = MainClientListener.CommandSet.ALIAS_PARAM;
        alias = obtain(0);
    }

    @Override
    protected boolean notEmpty() {
        return alias != null && !alias.isEmpty();
    }

    @Override
    public void execute() {
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
