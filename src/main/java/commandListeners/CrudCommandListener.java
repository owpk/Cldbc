package commandListeners;

import commands.CommandSet;
import commands.SetTableCmd;
import connection.DBConnection;
import core.AbsCommandListener;
import core.Client;
import core.Commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class CrudCommandListener extends AbsCommandListener {
    private final DBConnection dbConnection;
    private Connection connection;
    private final String alias;

    public CrudCommandListener(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
        alias = dbConnection.getCfg().getAlias();
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
            if (!query.isEmpty()) {
                if (!super.listenCommands(query)) {
                    query = query.toLowerCase().trim();
                    if (query.equals(CommandSet.BACK.getCommandText())) {
                        System.out.println("Connection closed");
                        Client.getClient().setCommandListener(Client.getClient().getMainListener());
                    } else if (query.startsWith("insert") ||
                            query.startsWith("delete") ||
                            query.startsWith("create"))
                        updCommand(query);
                    else if (query.startsWith("select"))
                        select(query);
                    else if (query.startsWith(CommandSet.SET_TABLE.getCommandText())) {
                        commandService(new SetTableCmd(query, alias));
                        createConnection();
                    } else {
                        System.out.println("Unknown command");
                        Commands.printHelp();
                    }
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
        drawTable(query);
    }

    protected void drawTable(String query) {
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


    public void close() {
        try {
            connection.close();
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }
}
