package cldbc.connection;

import cldbc.util.ConfigParams;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MongoDBConn extends DBConnection {

    /**DBC Driver class name: mongodb.jdbc.MongoDriver
     * URL format:jdbc:mongo://<\serverName>/<\databaseName>
     * e.g. url="jdbc:mongo://ds029847.mongolab.com:29847/tpch";
     * Con = DriverManager.getConnection(url, "dbuser", "dbuser");
     */

    public MongoDBConn(ConfigParams cfg) {
        super(cfg);
        urlPrefix = "jdbc:mongo://";
    }

    @Override
    public Connection createConn() throws ClassNotFoundException, SQLException {
        Class.forName("mongodb.jdbc.MongoDriver");
        return DriverManager.getConnection(createURL(), cfg.getUserName(), cfg.getUserName());
    }

}
