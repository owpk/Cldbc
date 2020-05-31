package core;

import commands.*;
import core.commands.*;
import connection.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class ClientManager {

    protected final ConnectionManager connectionManager;
    protected Connection connection;
    protected DBConnection dbConnection;
    protected final Scanner sc;

    public ClientManager() {
        connectionManager = new ConnectionManager();
        sc = new Scanner(System.in);
    }

    protected void commandService(Command c) {
        c.execute();
    }

    public void commandListener() {
        while (true) {
            System.out.print("cldbc> ");
            String command = sc.nextLine();
            command = command.toLowerCase().trim();

            if (command.equals(CommandSet.EXIT.getCommandText())) {
                System.out.println("Exit");
                break;
            } else if (command.startsWith(CommandSet.ALIAS_PARAM.getCommandText()))
                commandService(new AliasParamCmd(command));
            else if (command.equals(CommandSet.CONFIG.getCommandText()))
                commandService(new ConfigCmd(command));
            else if (command.equals(CommandSet.ALIAS.getCommandText()))
                commandService(new ConfigCmd());
            else if (command.equals(CommandSet.HELP.getCommandText()) || command.equals("?"))
                Commands.printHelp();
            else if (command.startsWith(CommandSet.CONNECT.getCommandText()))
                commandService(new ConnectionCmd(command));
            else if (command.startsWith(CommandSet.SET_TABLE.getCommandText()))
                commandService(new SetTableCmd(command));
            else {
                System.out.println("Unknown command");
                System.out.println("If you want to init connection use - connect 'alias_name'");
                Commands.printHelp();
            }
        }
        closeResources();
    }

    private void closeResources() {
        sc.close();
        try {
            connection.close();
        } catch (SQLException s) {
            System.out.println("ERROR DB connection has not closed");
        }
    }

}
