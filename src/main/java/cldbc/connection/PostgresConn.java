package cldbc.connection;

import cldbc.util.ConfigParams;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgresConn extends DBConnection {

    /**DBC Driver class name: mongodb.jdbc.MongoDriver
     * URL format:jdbc:mongo://<\serverName>/<\databaseName>
     * e.g. url="jdbc:mongo://ds029847.mongolab.com:29847/tpch";
     * Con = DriverManager.getConnection(url, "dbuser", "dbuser");
     */

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

