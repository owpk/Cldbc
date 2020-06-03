package core;

import connection.DBConnection;
import connection.MySqlConn;
import connection.PostgresConn;
import util.ConfigParams;
import util.ConfigReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionManager {
    private final List<ConfigParams> configParams;
    private final Map<String, DBConnection> connectionList;
    private static ConnectionManager connectionManager;

    public Map<String, DBConnection> getConnectionList() {
        return connectionList;
    }

    private void createCon(String alias, DBConnection c) {
        connectionList.put(alias, c);
    }

    public static ConnectionManager getManager() {
        if (connectionManager == null) {
            connectionManager = new ConnectionManager();
            connectionManager.fillConnectionList();
        }
        return connectionManager;
    }

    private void fillConnectionList(){
        for (ConfigParams cfg : configParams) {
            switch (cfg.getVendor()) {
                case "mysql" :
                    createCon(cfg.getAlias(), new MySqlConn(cfg));
                    break;
                case "postgres" :
                    createCon(cfg.getAlias(), new PostgresConn(cfg));
                    break;
            }
        }
    }

    private ConnectionManager() {
        connectionList = new HashMap<>();
        configParams = ConfigReader.getConfigList();
    }


}
