package connection;

import util.ConfigParams;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DBConnection {
    protected ConfigParams cfg;

    protected String urlPrefix;

    public DBConnection(ConfigParams cfg) {
        this.cfg = cfg;
    }

    protected String createURL() {
        String url = urlPrefix + cfg.getHost() + ":" + cfg.getPort() + "/" + cfg.getDbName() + cfg.getParams();
        System.out.println(url);
        return url;
    }

    public Connection createConn() throws ClassNotFoundException, SQLException {
        return DriverManager.getConnection(createURL(), cfg.getUserName(), cfg.getUserPass());
    }

    public ConfigParams getCfg() {
        return cfg;
    }

    public void setCfg(ConfigParams cfg) {
        this.cfg = cfg;
    }

}
