package core.connection;

import core.ConfigParams;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgresConn extends DBConnection {

    public PostgresConn(ConfigParams cfg) {
        super(cfg);
        urlPrefix = "jdbc:postgresql://";
    }

    @Override
    protected String createURL() {
        return urlPrefix + host + ":" + port + "/" + urlParams;
    }

    @Override
    public Connection createConn() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        return super.createConn();
    }

}

