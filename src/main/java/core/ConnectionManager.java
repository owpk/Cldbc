package core;

import core.connection.DBConnection;
import core.connection.MySqlConn;
import core.connection.PostgresConn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionManager {
    private DBConnection conn;
    private List<ConfigParams> configParams;
    private Map<String, DBConnection> connectionList;

    public Map<String, DBConnection> getConnectionList() {
        return connectionList;
    }

    public ConnectionManager() {
        connectionList = new HashMap<>();
        configParams = ConfigReader.getConfigList();
        for (ConfigParams cfg : configParams) {
            switch (cfg.getVendor()) {
                case "mysql" :
                    conn = new MySqlConn(cfg);
                    connectionList.put(cfg.getAlias(), conn);
                    break;
                case "postgres" :
                    conn = new PostgresConn(cfg);
                    connectionList.put(cfg.getAlias(), conn);
                    break;
            }
        }
    }


}
