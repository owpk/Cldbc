package core;

import core.connection.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.Function;

public class ClientManager {

    private final ConnectionManager connectionManager;
    private Connection connection;
    private DBConnection dbConnection;
    private Scanner sc;

    public ClientManager() {
        connectionManager = new ConnectionManager();
        sc = new Scanner(System.in);
    }

    private abstract class Command {
        protected String rowCommandLine;
        protected String[] rowCommands;
        protected String name;
        protected Map<String, String> params;

        public void setName(String name) {
            this.name = name;
        }

        public Command(String rowCommandLine) {
            this.rowCommandLine = rowCommandLine;
            params = new HashMap<>();
            rowCommands = rowCommandLine.split(" ");
            name = rowCommands[0].trim();
            parseCommand();
        }

        protected abstract void parseCommand();

        public void putCommand(String key, String value) {
            params.put(key, value);
        }

        public Map<String, String> getParams() {
            return params;
        }
    }

    private enum Commands {
        EXIT("exit", ""),
        ALIAS("al", " - (aliases) shows available aliases"),
        CONFIG("cfg", " - (config) shows current connection configuration (only works if the connection is established)"),
        HELP("help", ""),
        CONNECT("con", " - (connection) usage: con 'alias_name'"),
        BACK("back", " - back from CRUD menu"),
        ALIAS_PARAM("alp", " - (alias parameters) usage: alp 'alias_name'. Shows selected alias parameters"),
        SET_TABLE("stb", " - (set table) usage: if connected - stb 'table_name' else - stb 'alisa_name' 'db_name'");
        private final String commandText;
        private final String commandDescription;

        Commands(String commandText, String commandDescription) {
            this.commandDescription = commandDescription;
            this.commandText = commandText;
        }
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
                if (query.equals(Commands.BACK.commandText)) {
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
                else if (query.equals(Commands.CONFIG.commandText))
                    showConnectionConfig();
                else if (query.startsWith(Commands.SET_TABLE.commandText)) {
                    Command c = new DBCommand(query);
                    setDBName(c);
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
        String dbName = c.params.get(1);
        String alias = c.params.get(0);
        if (dbName.equals("")) {
            System.out.println("Wrong dbName");
            System.out.println(Commands.SET_TABLE.commandText + Commands.SET_TABLE.commandDescription);
            return;
        }
        connectionManager.getConnectionList().get(alias).getCfg().setDbName(dbName);
    }

    public void commandListener() {
        while (true) {
            System.out.print("cldbc> ");
            String command = sc.nextLine();
            command = command.toLowerCase().trim();

            if (command.equals(Commands.EXIT.commandText)) {
                System.out.println("Exit");
                break;
            } else if (command.startsWith(Commands.ALIAS_PARAM.commandText)) {
                printAliasConfig(command);
            } else if (command.equals(Commands.CONFIG.commandText)) {
                System.out.println("No connection detected");
                System.out.println("To init connection use: " + Commands.CONNECT.commandText + " 'alias_name'");
                showAvailableAliasList();
            } else if (command.equals(Commands.ALIAS.commandText))
                showAvailableAliasList();
            else if (command.equals(Commands.HELP.commandText) || command.equals("?")) {
                printHelp();
            } else if (command.startsWith(Commands.CONNECT.commandText)) {
                connect(command);
            } else if (command.startsWith(Commands.SET_TABLE.commandText)) {
                setDBName(new Command(command));
            } else {
                System.out.println("Unknown command");
                System.out.println("If you want to init connection use - connect 'alias_name'");
                printHelp();
            }
        }
        closeResources();
    }

    private void printAliasConfig(String command) {
        Command c = new Command(command);
        String alias = c.params.get(0);
        if (alias.isEmpty()) {
            System.out.println("Wrong usage");
            System.out.println(Commands.ALIAS_PARAM.commandText + Commands.ALIAS_PARAM.commandDescription);
            showAvailableAliasList();
        } else {
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
        Commands[] commands = Commands.values();
        System.out.println("Available commands: ");
        Arrays.stream(commands).forEach(x -> System.out.println(x.commandText + "  " + x.commandDescription));
    }

    private void connect(String command) {
        sc.reset();
        Command c = new Command(command);
        String alias = c.params.get(0);
        if (alias.isEmpty()) {
            System.out.println("Select available aliases");
            showAvailableAliasList();
            alias = sc.nextLine();
        }
        if (!alias.isEmpty()) {
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
