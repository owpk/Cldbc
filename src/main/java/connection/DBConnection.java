package connection;

import util.ConfigParams;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DBConnection {
    protected ConfigParams cfg;
    protected String port;
    protected String host;
    protected String user;
    protected String pass;

    protected String urlParams;
    protected String dbName;

    protected String urlPrefix;

    public DBConnection(ConfigParams cfg) {
        this.cfg = cfg;
        executeCfg();
    }

    protected String createURL() {
        String url = urlPrefix + host + ":" + port + "/" + dbName + urlParams;
        System.out.println(url);
        return url;
    }

    public Connection createConn() throws ClassNotFoundException, SQLException {
        return DriverManager.getConnection(createURL(), user, pass);
    }

    public ConfigParams getCfg() {
        return cfg;
    }

    public void setCfg(ConfigParams cfg) {
        this.cfg = cfg;
        executeCfg();
    }

    private void executeCfg() {
        port = cfg.getPort();
        host = cfg.getHost();
        user = cfg.getUserName();
        pass = cfg.getUserPass();
        dbName = cfg.getDbName();
        urlParams = cfg.getParams();
    }

}
