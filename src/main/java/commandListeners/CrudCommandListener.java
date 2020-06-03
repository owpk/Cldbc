package commandListeners;

import commands.CommandSet;
import commands.SetTableCmd;
import connection.DBConnection;
import core.Client;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class CrudCommandListener extends BaseListener {
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
        createConnection();
    }

    private void createConnection() {
        try {
            this.connection = dbConnection.createConn();
            System.out.println("DB Connected, alias: " + dbConnection.getCfg().getAlias());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean listenCommands(String query) {
        if (!super.listenCommands(query)) {
            query = query.toLowerCase().trim();
            if (query.equals(CommandSet.CLOSE.getCommandText())) {
                System.out.println("Connection closed");
                close();
                Client.getClient().setCommandListener(Client.getClient().getMainListener());
            } else if (query.startsWith("insert") ||
                    query.startsWith("delete") ||
                    query.startsWith("create"))
                updCommand(query);
            else if (query.startsWith("select")) {
                select(query);
                drawTable(res);
            }
            else if (query.equals(CommandSet.CONFIG.getCommandText()))
                showConnectionConfig();
            else if (query.startsWith(CommandSet.SET_TABLE.getCommandText())) {
                commandService(new SetTableCmd(query, alias));
                createConnection();
            } else if(query.equals("show")) {
                drawTable(selectRow(2));
            } else {
                printUnknownCommandStack();
            }
        }
        return false;
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


    private void select(String query) {
        boolean flag = true;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            columns = rs.getMetaData().getColumnCount();
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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private ArrayList<String>[] selectRow(int index) {
        ArrayList<String>[] tempArr = new ArrayList[columns];
        for (int i = 0; i < tempArr.length; i++) {
            tempArr[i] = new ArrayList<>();
            tempArr[i].add(res[i].get(0));
            tempArr[i].add(res[i].get(index));
        }
        return tempArr;
    }


    private void drawTable(ArrayList<String>[] arrayOfLists) {
        colInd = 0;
        String[] lines = new String[columns];
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
        boolean canWrite;
        //print data
        for (int i = 0; i < arrayOfLists[colInd].size(); i++) {
            if (colInd++ == columns - 1)
                colInd = 0;
            canWrite = row == 1 || row == 0;
            row++;
            if (canWrite) {
                Arrays.stream(lines).forEach(System.out::print);
                System.out.println("");
            }
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
