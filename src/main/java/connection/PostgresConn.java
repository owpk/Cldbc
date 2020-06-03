package connection;

import util.ConfigParams;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgresConn extends DBConnection {

    public PostgresConn(ConfigParams cfg) {
        super(cfg);
        urlPrefix = "jdbc:postgresql://";
    }


    @Override
    public Connection createConn() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        return super.createConn();
    }

}

