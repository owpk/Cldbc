package core.commands;


public class MenuCommands extends CommandImpl implements Command {

    public MenuCommands(String rowCommandLine) {
        super(rowCommandLine);
    }

    @Override
    public String getAlias() throws ZeroArgsException {
        return params.get(0);
    }

    @Override
    public String getDBName() throws ZeroArgsException {
        return params.get(1);
    }
}
