package core.connection;

import core.ConfigParams;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlConn extends DBConnection {

    public MySqlConn(ConfigParams cfg) {
        super(cfg);
        urlPrefix = "jdbc:mysql://";
    }

    @Override
    public Connection createConn() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return super.createConn();
    }
}
