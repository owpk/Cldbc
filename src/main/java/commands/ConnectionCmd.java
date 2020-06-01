package commands;

import core.Commands;

import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionCmd extends Commands implements Command {
    private String alias;

    public ConnectionCmd(String command) {
        super(command);
        cmd = CommandSet.CONNECT;
        alias = obtain(0);
    }

    @Override
    protected boolean notEmpty() {
        return alias != null && !alias.isEmpty();
    }

    private class ConnectionOperator {
        private String alias;

        public ConnectionOperator(String alias) {
            this.alias = alias;
        }

        private void updCommand(String query) {
            try (Statement stmt = connection.createStatement()) {
                int u = stmt.executeUpdate(query);
                System.out.println("Success, rows changed: " + u);
            } catch (SQLException s) {
                System.out.println(s.getMessage());
            }
        }

        private void select(String query) {
            drawTable(query);
        }

        private void initSession() throws SQLException {
            System.out.println("DB Connected, alias: " + dbConnection.getCfg().getAlias());
            //showConnectionConfig();
            sc.reset();
            while (true) {
                System.out.print(alias + "> ");
                String query = sc.nextLine();
                if (!query.isEmpty()) {
                    query = query.toLowerCase().trim();
                    if (query.equals(CommandSet.BACK.getCommandText())) {
                        System.out.println("Connection closed");
                        sc.reset();
                        break;
                    } else if (query.equals("?") || query.equals(CommandSet.HELP.getCommandText()))
                        printHelp();
                    else if (
                            query.startsWith("insert") ||
                                    query.startsWith("delete") ||
                                    query.startsWith("create"))
                        updCommand(query);
                    else if (query.startsWith("select"))
                        select(query);
                    else if (query.equals(CommandSet.CONFIG.getCommandText()))
                        showConnectionConfig();
                    else if (query.startsWith(CommandSet.SET_TABLE.getCommandText())) {
                        new SetTableCmd(query, alias, sc).execute();
                        connection.close();
                        execute();
                    } else System.out.println("Unknown command");
                }
            }
        }
    }

    @Override
    public void execute() {
        sc.reset();
        dbConnection = CONNECTION_MANAGER.getConnectionList().get(alias);
        try {
            connection = dbConnection.createConn();
            new ConnectionOperator(alias).initSession();
            connection.close();
        } catch (SQLException | ClassNotFoundException s) {
            System.out.println(s.getMessage());
        }
    }

    @Override
    public void handleException() {
        printWarning();
        printThisCommandHelp();
        showAvailableAliasList();
    }
}
