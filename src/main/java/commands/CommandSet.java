package commands;

public enum CommandSet {
    EXIT("exit", ""),
    ALIAS("al", " - (aliases) shows available aliases"),
    CONFIG("cfg", " - (config) shows current connection configuration. Usage: cfg 'alias_name', from CRUD menu - cfg"),
    HELP("help", ""),
    CONNECT("con", " - (connection) usage: con 'alias_name'"),
    BACK("back", " - back from CRUD menu"),
    ALIAS_PARAM("alp", " - (alias parameters) usage: alp 'alias_name'. Shows selected alias parameters"),
    SET_TABLE("use", " - (use table) usage: if connected - use 'table_name' else - use 'alisa_name' 'db_name'");
    private final String commandText;
    private final String commandDescription;

    CommandSet(String commandText, String commandDescription) {
        this.commandDescription = commandDescription;
        this.commandText = commandText;
    }

    public String getCommandText() {
        return commandText;
    }

    public String getCommandDescription() {
        return commandDescription;
    }
}
