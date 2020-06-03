package commands;

public enum CommandSet {
    EXIT("exit", " - Exit"),
    ALIAS("al", " - shows available aliases"),
    CONFIG("cfg", " - usage: cfg 'alias_name'; Shows current connection configuration"),
    HELP("help", " - shows available commands"),
    CONNECT("con", " - usage: con 'alias_name'"),
    CLOSE("close", " - close current connection"),
    ALIAS_PARAM("alp", " - usage: alp 'alias_name'; Shows selected alias parameters"),
    SET_TABLE("set", " - usage: set 'alisa_name' 'db_name', if connected - set 'table_name'");
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
