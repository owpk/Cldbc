package cldbc;

import cldbc.commands.CommandInt;
import cldbc.commands.SetTableCmd;
import cldbc.connection.DBConnection;
import cldbc.connection.MySqlConn;
import cldbc.core.ConnectionManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import cldbc.util.ConfigParams;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SetDataBaseNameTest {
    private ConfigParams cfg;
    private CommandInt setTableCmd;
    private ConnectionManager connectionManager;
    private Field connectionList;
    private Field singletonInConnectionManager;

    @Before
    public void init() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        cfg = createTestConfig();
        Constructor<?> constructor = ConnectionManager.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        connectionManager = (ConnectionManager) constructor.newInstance();

        connectionList = ConnectionManager.class.getDeclaredField("connectionList");
        singletonInConnectionManager = ConnectionManager.class.getDeclaredField("connectionManager");
        connectionList.setAccessible(true);
        singletonInConnectionManager.setAccessible(true);
    }

    @Test
    public void shouldChangeDataBaseName() throws IllegalAccessException {
        cfg.setDbName("Change this name please!!!");

        DBConnection dbConnection = new MySqlConn(cfg);

        Map<String, DBConnection> connectionMap = new HashMap<>();
        connectionMap.put("testAlias", dbConnection);
        connectionList.set(connectionManager, connectionMap);

        singletonInConnectionManager.set(connectionManager, connectionManager);

        setTableCmd = new SetTableCmd("use sakila", "testAlias");
        System.out.println("DB name before: " + connectionManager.getConnectionList().get("testAlias").getCfg().getDbName());
        setTableCmd.execute();
        Assert.assertEquals("sakila", connectionManager.getConnectionList().get("testAlias").getCfg().getDbName());
    }

    @After
    public void destroy() {
        connectionList = null;
    }

    private static ConfigParams createTestConfig() {
        ConfigParams cfg = new ConfigParams();
        Arrays.stream(cfg.getClass().getDeclaredMethods())
                .filter(m -> {
                    Class<?>[] params = m.getParameterTypes();
                    Class<?> parameter;
                    if (params.length > 0) {
                        parameter = params[0];
                        if (m.getName().startsWith("set") && parameter.equals(String.class)) {
                            m.setAccessible(true);
                            return true;
                        }
                    }
                    return false;
                }).forEach(m -> {
            try {
                m.invoke(cfg, "1");
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        return cfg;
    }

}
