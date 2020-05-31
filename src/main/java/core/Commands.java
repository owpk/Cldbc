package core;

import commands.Command;
import commands.CommandSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public abstract class Commands extends ClientManager implements Command {
    private String rowCmd;
    private Map<String, String> paramsPool;
    protected CommandSet cmd;

    public Commands(String rowCmd) {
        this.rowCmd = rowCmd;
        paramsPool = new HashMap<>();
    }

    public Commands() {
    }

    protected String obtain(String element) {
        parseRowCommand();
        if (paramsPool.get(element) != null) {
            return paramsPool.get(element);
        }
        else printThisCommandHelp();
        return null;
    }

    protected abstract void parseRowCommand();

    protected void printThisCommandHelp() {
        System.out.println(cmd.getCommandText()+cmd.getCommandDescription());
    }

    public static void printHelp() {
        CommandSet[] commands = CommandSet.values();
        System.out.println("Available commands: ");
        Arrays.stream(commands).forEach(x -> System.out.println(x.getCommandText() + x.getCommandDescription()));
    }

    protected void showAvailableAliasList() {
        System.out.print("Available aliases: ");
        connectionManager.getConnectionList().forEach((key, value) -> System.out.print(key + " | "));
        System.out.println("");
    }

    public void showConnectionConfig() {
        System.out.println("Connection configurations ======== ");
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
