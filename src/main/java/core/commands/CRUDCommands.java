package core.commands;


public class CRUDCommands extends CommandImpl implements Command {
    private String alias;

    public CRUDCommands(String rowCommandLine, String alias) {
        super(rowCommandLine);
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public String getDBName() {
        return params.get(0);
    }
}
