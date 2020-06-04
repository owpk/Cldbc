package cldbc.commandListeners;

import cldbc.commands.ConnectionCmd;
import cldbc.commands.SetTableCmd;
import cldbc.connection.DBConnection;
import cldbc.core.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class CrudCommandListener extends BaseListener {
    private static final Logger logger = LogManager.getLogger(CrudCommandListener.class.getName());
    private final DBConnection dbConnection;
    private Connection connection;
    private final String alias;
    private ArrayList<String>[] res;
    private int columns;
    private int colInd;

    public CrudCommandListener(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
        alias = dbConnection.getCfg().getAlias();
        listenerName = alias;
    }

    public static Logger getLogger() {
        return logger;
    }

    public enum CommandSet {
        SELECT("select", ""),
        INSERT("insert", ""),
        CREATE("create", ""),
        DELETE("delete", ""),
        SHOW("show", "usage: show 'row_index'; show 'start_index'-'end_index'");
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

    public void createConnection() throws SQLException, ClassNotFoundException {
            this.connection = dbConnection.createConn();
            System.out.println("DB Connected, alias: " + dbConnection.getCfg().getAlias());
    }

    @Override
    public boolean listenCommands(String query) {
        if (!super.listenCommands(query)) {
            query = query.toLowerCase().trim();
            if (query.equals(MainClientListener.CommandSet.CLOSE.getCommandText())) {
                System.out.println("Connection closed");
                close();
                Client.getClient().setCommandListener(Client.getClient().getMainListener());
            } else if (query.startsWith(CommandSet.INSERT.commandText) ||
                    query.startsWith(CommandSet.DELETE.commandText) ||
                    query.startsWith(CommandSet.CREATE.commandText))
                updCommand(query);
            else if (query.startsWith(CommandSet.SELECT.commandText)) {
                if (select(query))
                    drawTable(res);
            } else if (query.equals(MainClientListener.CommandSet.CONFIG.getCommandText()))
                showConnectionConfig();
            else if (query.startsWith(MainClientListener.CommandSet.SET_TABLE.getCommandText())) {
                commandService(new SetTableCmd(query, alias));
                commandService(new ConnectionCmd(query, alias));
            } else if (query.startsWith(CommandSet.SHOW.commandText)) {
                parse(query);
            } else {
                printUnknownCommandStack();
            }
        }
        return false;
    }

    private void parse(String s) {
        String data = s.substring(CommandSet.SHOW.commandText.length()).trim();
        if (data.matches("\\d+\\s*-\\s*\\d+")) {
            drawTable(selectRow(Integer.parseInt(data.substring(0, data.indexOf("-")).trim()),
                    Integer.parseInt(data.substring(data.indexOf("-") + 1).trim())));
        } else if (data.matches("\\d+")) {
            drawTable(selectRow(Integer.parseInt(data)));
        } else {
            System.out.println("wrong format");
            System.out.println(CommandSet.SELECT.commandText + CommandSet.SELECT.commandDescription);
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

    public void showConnectionConfig() {
        System.out.println("Connection configurations ");
        System.out.println(dbConnection.getCfg().showParams());
        System.out.println("====================");
    }

    private boolean select(String query) {
        boolean flag = true;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            columns = rs.getMetaData().getColumnCount();

            if (columns == 0)
                return false;

            res = new ArrayList[columns];

            //fill res with empty columns
            for (int i = 0; i < res.length; i++) {
                res[i] = new ArrayList<>();
            }

            //fill empty columns with data
            while (rs.next()) {
                for (int i = 1; i <= columns; i++) {
                    if (flag)
                        res[i - 1].add(" " + rs.getMetaData().getColumnLabel(i));
                    res[i - 1].add(" " + rs.getString(i));
                }
                flag = false;
            }

            //create spaces
            for (ArrayList<String> re : res) {
                OptionalInt opt = re.stream().filter(Objects::nonNull).mapToInt(String::length).max();
                int length = opt.isPresent() ? opt.getAsInt() : "null".length();
                for (int i = 0; i < re.size(); i++) {
                    StringBuilder space = new StringBuilder("  ");
                    if (re.get(i) != null) {
                        for (int k = 0; k < length - re.get(i).length(); k++)
                            space.append(" ");
                        re.set(i, re.get(i) + space);
                    } else {
                        for (int j = 0; j < length - "null".length(); j++)
                            space.append(" ");
                        re.set(i, "    " + space);
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private ArrayList<String>[] selectRow(int index) {
        System.out.println(index);
        ArrayList<String>[] tempArr = new ArrayList[columns];
        for (int i = 0; i < columns; i++) {
            tempArr[i] = new ArrayList<>();
            tempArr[i].add(res[i].get(0));
            tempArr[i].add(res[i].get(index));
        }
        return tempArr;
    }

    private ArrayList<String>[] selectRow(int start, int end) {
        start -=1;
        end -=1;
        colInd = 0;
        ArrayList<String>[] tempArr = new ArrayList[columns];
        if (end - start <= 0)
            return null;

        boolean flag;
        int index = 0;
        for (int i = 0; i < columns * (end - start); i++) {
            if (colInd == columns) {
                index++;
                colInd = 0;
            }
            if (flag = i < columns) {
                tempArr[colInd] = new ArrayList<>();
            }
            if (flag)
                tempArr[colInd].add(res[colInd].get(0));
            tempArr[colInd].add(res[colInd].get(index + 1 + start));
            colInd++;
        }
        return tempArr;
    }


    private void drawTable(ArrayList<String>[] arrayOfLists) {
        colInd = 0;
        String[] lines = new String[columns];
        if (arrayOfLists == null)
            return;
        //create horizontal lines
        for (ArrayList<String> strings : arrayOfLists) {
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

        //print data
        for (int i = 0; i < arrayOfLists[colInd++].size(); i++) {
            if (colInd == columns)
                colInd = 0;
            if (row == 1 || row == 0) {
                Arrays.stream(lines).forEach(System.out::print);
                System.out.println("");
            }
            row++;
            for (ArrayList<String> re : arrayOfLists)
                System.out.print("|" + re.get(i));
            System.out.println("|");
        }
        Arrays.stream(lines).forEach(System.out::print);
        System.out.println("");
        System.out.println("DB rows: " + (row - 1));
        System.out.println("DB columns: " + res.length);
    }



    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }
}
