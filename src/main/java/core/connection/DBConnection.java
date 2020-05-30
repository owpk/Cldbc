package core.connection;

import core.ConfigParams;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    protected String port;
    protected String host;
    protected String user;
    protected String pass;

    protected String urlParams;
    protected String dbName;

    protected String urlPrefix;
    protected ConfigParams cfg;

    public DBConnection(ConfigParams cfg) {
        this.cfg = cfg;
        port = cfg.getPort();
        host = cfg.getHost();
        user = cfg.getUserName();
        pass = cfg.getUserPass();
        dbName = cfg.getDbName();
        urlParams = cfg.getParams();
    }

    protected String createURL() {
        return urlPrefix + host + ":" + port + "/" + dbName + urlParams;
    }

    public Connection createConn() throws ClassNotFoundException, SQLException {
        return DriverManager.getConnection(createURL(), user, pass);
    }

    public ConfigParams getCfg() {
        return cfg;
    }

}
