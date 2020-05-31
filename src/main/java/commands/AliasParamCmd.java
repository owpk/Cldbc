package commands;

import core.Commands;

public class AliasParamCmd extends Commands implements Command {
    private String alias;

    public AliasParamCmd(String command) {
        super(command);
        cmd = CommandSet.ALIAS_PARAM;
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
                    connectionManager
                            .getConnectionList()
                            .get(alias)
                            .getCfg()
                            .showParams());
        }
    }

    @Override
    public void handleException() {
        printThisCommandHelp();
        showAvailableAliasList();
    }
}
