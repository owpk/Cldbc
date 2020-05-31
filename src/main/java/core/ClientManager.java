package core;

import core.commands.CRUDCommands;
import core.commands.Command;
import core.commands.CommandSet;
import core.commands.MenuCommands;
import core.connection.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class ClientManager {

    private final ConnectionManager connectionManager;
    private Connection connection;
    private DBConnection dbConnection;
    private Scanner sc;
    private Command command;

    public ClientManager() {
        connectionManager = new ConnectionManager();
        sc = new Scanner(System.in);
    }

    private class ConnectionOperator {
        private String alias;

        public ConnectionOperator(String alias) {
            this.alias = alias;
        }

        private void initSession() {
            System.out.println("DB Connected, alias: " + dbConnection.getCfg().getAlias());
            showConnectionConfig();
            sc.reset();
            while (true) {
                System.out.print("CRUD> ");
                String query = sc.nextLine();
                query = query.toLowerCase().trim();
                if (query.equals(CommandSet.BACK.getCommandText())) {
                    System.out.println("Connection closed");
                    sc.reset();
                    break;
                } else if (query.equals("?") || query.equals("help"))
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
                    setDBName(new CRUDCommands(query, alias));
                } else System.out.println("Unknown command");

            }
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
    }

    private void setDBName(Command c) {
        if (c.checkZeroParams() || c.getParams().size() == 1) {
            System.out.println("Wrong usage");
            System.out.println(CommandSet.SET_TABLE.getCommandText() + CommandSet.SET_TABLE.getCommandDescription());
            return;
        } else {
            String dbName = c.getDBName();
            String alias = c.getAlias();
            connectionManager.getConnectionList().get(alias).getCfg().setDbName(dbName);
        }
    }

    public void commandListener() {
        while (true) {
            System.out.print("cldbc> ");
            String command = sc.nextLine();
            command = command.toLowerCase().trim();

            if (command.equals(CommandSet.EXIT.getCommandText())) {
                System.out.println("Exit");
                break;
            } else if (command.startsWith(CommandSet.ALIAS_PARAM.getCommandText())) {
                printAliasConfig(new MenuCommands(command));
            } else if (command.equals(CommandSet.CONFIG.getCommandText())) {
                System.out.println("No connection detected");
                System.out.println("To init connection use: " + CommandSet.CONNECT.getCommandText() + " 'alias_name'");
                showAvailableAliasList();
            } else if (command.equals(CommandSet.ALIAS.getCommandText()))
                showAvailableAliasList();
            else if (command.equals(CommandSet.HELP.getCommandText()) || command.equals("?")) {
                printHelp();
            } else if (command.startsWith(CommandSet.CONNECT.getCommandText())) {
                connect(new MenuCommands(command));
            } else if (command.startsWith(CommandSet.SET_TABLE.getCommandText())) {
                setDBName(new MenuCommands(command));
            } else {
                System.out.println("Unknown command");
                System.out.println("If you want to init connection use - connect 'alias_name'");
                printHelp();
            }
        }
        closeResources();
    }

    private void printAliasConfig(Command command) {
        String alias;
        if (command.checkZeroParams()) {
            System.out.println("Wrong usage");
            System.out.println(CommandSet.ALIAS_PARAM.getCommandText() + CommandSet.ALIAS_PARAM.getCommandDescription());
            showAvailableAliasList();
        } else {
            alias = command.getAlias();
            try {
                System.out.println(
                        connectionManager.getConnectionList()
                                .get(alias).getCfg().showParams());
            } catch (NullPointerException b) {
                System.out.println("Wrong alias");
            }
        }
    }

    private void showAvailableAliasList() {
        System.out.print("Available aliases: ");
        connectionManager.getConnectionList().forEach((key, value) -> System.out.print(key + " | "));
        System.out.println("");
    }

    private void showConnectionConfig() {
        System.out.println("Connection configurations ======== ");
        System.out.println(dbConnection.getCfg().showParams());
        System.out.println("====================");
    }

    private void printHelp() {
        CommandSet[] commands = CommandSet.values();
        System.out.println("Available commands: ");
        Arrays.stream(commands).forEach(x -> System.out.println(x.getCommandText() + "  " + x.getCommandDescription()));
    }

    private void connect(Command command) {
        sc.reset();
        String alias;
        if (command.checkZeroParams()) {
            System.out.println("Select available aliases");
            showAvailableAliasList();
            alias = sc.nextLine();
        } else alias = command.getAlias();
        dbConnection = connectionManager.getConnectionList().get(alias);
        if (dbConnection == null) {
            System.out.println("Wrong alias");
            return;
        }
        try {
            connection = dbConnection.createConn();
            new ConnectionOperator(alias).initSession();
            connection.close();
        } catch (SQLException | ClassNotFoundException s) {
            System.out.println(s.getMessage());
        }
    }


    private void closeResources() {
        sc.close();
        try {
            connection.close();
        } catch (SQLException s) {
            System.out.println("ERROR DB connection has not closed");
        }
    }

    private void drawTable(String query) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            int columns = rs.getMetaData().getColumnCount();
            ArrayList<String>[] res = new ArrayList[columns];
            for (int i = 0; i < res.length; i++) {
                res[i] = new ArrayList<>();
            }

            int colInd;
            boolean flag = true;

            while (rs.next()) {
                for (int i = 1; i <= columns; i++) {
                    if (flag)
                        res[i - 1].add(" " + rs.getMetaData().getColumnLabel(i));
                    res[i - 1].add(" " + rs.getString(i));
                }
                flag = false;
            }

            for (ArrayList<String> re : res) {
                OptionalInt opt = re.stream().filter(Objects::nonNull).mapToInt(String::length).max();
                int length = opt.isPresent() ? opt.getAsInt() : "null".length();
                for (int i = 0; i < re.size(); i++) {
                    StringBuilder space = new StringBuilder("  ");
                    if (re.get(i) != null) {
                        for (int k = 0; k < length - re.get(i).length(); k++) {
                            space.append(" ");
                        }
                        re.set(i, re.get(i) + space);
                    } else {
                        for (int j = 0; j < length - "null".length(); j++)
                            space.append(" ");
                        re.set(i, "    " + space);
                    }
                }
            }

            colInd = 0;
            boolean canWrite;
            String[] lines = new String[columns];

            for (ArrayList<String> strings : res) {
                StringBuilder line = new StringBuilder("+");
                for (int j = 0; j < strings.get(0).length(); j++)
                    line.append("-");
                lines[colInd] = line.toString();
                if (colInd == lines.length - 1)
                    lines[colInd] = lines[colInd].concat("+");
                colInd++;
            }

            colInd = 0;
            int row = 0;

            for (int i = 0; i < res[colInd].size(); i++) {
                if (colInd++ == columns - 1)
                    colInd = 0;
                canWrite = row == 1 || row == 0;
                row++;
                if (canWrite) {
                    Arrays.stream(lines).forEach(System.out::print);
                    System.out.println("");
                }
                for (ArrayList<String> re : res)
                    System.out.print("|" + re.get(i));

                System.out.println("|");
            }
            Arrays.stream(lines).forEach(System.out::print);
            System.out.println("");
            System.out.println("DB rows: " + (row - 1));
            System.out.println("DB columns: " + res.length);
        } catch (SQLException s) {
            System.out.println(s.getMessage());
        }
    }

}
