package commands;

import core.Commands;

public class ConfigCmd extends Commands implements Command {
    public ConfigCmd(String command) {
        super(command);
        cmd = CommandSet.CONFIG;
    }

    public ConfigCmd() {
        cmd = CommandSet.CONFIG;
    }

    @Override
    protected void parseRowCommand() {

    }

    @Override
    public void execute() {
        System.out.println("No connection detected");
        System.out.println("To init connection use: " + cmd.getCommandText() + " 'alias_name'");
        showAvailableAliasList();
    }
}
