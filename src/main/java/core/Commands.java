package core;

import commands.Command;
import commands.CommandSet;
import connection.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Commands implements Command {
    protected static final ConnectionManager CONNECTION_MANAGER = ConnectionManager.getManager();
    protected List<String> rowCommandsPool;
    protected List<String> rowKeyList;
    protected Map<String, String> paramsPool;
    protected Map<String, String> keyPool;
    protected CommandSet cmd;
    protected DBConnection dbConnection;
    protected Scanner sc;
    protected Connection connection;

    public Commands(String rowCommand) {
        this.sc = ClientManager.getScanner();
        rowCommandsPool = new ArrayList<>(Arrays.asList(rowCommand.split(" ")));
        rowKeyList = new ArrayList<>();
        rowCommandsPool = rowCommandsPool.stream()
                .skip(1)
                .filter(x -> x != null && !x.isEmpty())
                .map(String::trim)
                .filter(x -> {
                    if (x.startsWith("-")) {
                        rowKeyList.add(x);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        paramsPool = new HashMap<>();
        keyPool = new HashMap<>();
    }

    public static void showAvailableAliasList() {
        System.out.print("Available aliases: ");
        CONNECTION_MANAGER.getConnectionList().forEach((key, value) -> System.out.print(key + " | "));
        System.out.println("");
    }

    public static void printHelp() {
        CommandSet[] commands = CommandSet.values();
        System.out.println("Available commands: ");
        Arrays.stream(commands).forEach(x -> System.out.println(x.getCommandText() + x.getCommandDescription()));
    }

    public Commands() {
    }

    protected String obtain(int element) {
        try {
            return rowCommandsPool.get(element);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    protected abstract boolean notEmpty();

    protected void printThisCommandHelp() {
        System.out.println(cmd.getCommandText() + cmd.getCommandDescription());
    }

    public void showConnectionConfig() {
        System.out.println("Connection configurations ");
        System.out.println(dbConnection.getCfg().showParams());
        System.out.println("====================");
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
}
