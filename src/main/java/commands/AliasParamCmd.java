package commands;

import core.Commands;

public class AliasParamCmd extends Commands implements Command {
    private String alias;

    public AliasParamCmd(String command) {
        super(command);
        cmd = CommandSet.ALIAS_PARAM;
    }

    @Override
    protected void parseRowCommand() {

    }

    @Override
    protected void printThisCommandHelp() {
        System.out.println("usage");
    }

    @Override
    public void execute() {
        System.out.println(
                connectionManager.getConnectionList()
                        .get(alias).getCfg().showParams());

    }
}
