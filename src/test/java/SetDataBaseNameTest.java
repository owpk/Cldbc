import commands.CommandInt;
import commands.SetTableCmd;
import connection.DBConnection;
import connection.MySqlConn;
import core.ConnectionManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.ConfigParams;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SetDataBaseNameTest {
    private ConfigParams cfg = createTestConfig();
    private CommandInt setTableCmd;
    private ConnectionManager connectionManager;
    private Field connectionList;
    private Field singleTonInConnectionManager;

    @Before
    public void init() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        Constructor<?> constructor = ConnectionManager.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        connectionManager = (ConnectionManager) constructor.newInstance();

        connectionList = ConnectionManager.class.getDeclaredField("connectionList");
        singleTonInConnectionManager = ConnectionManager.class.getDeclaredField("connectionManager");
        connectionList.setAccessible(true);
        singleTonInConnectionManager.setAccessible(true);

    }

    @Test
    public void shouldChangeDataBaseName() throws IllegalAccessException {
        cfg.setDbName("Change this name please!!!");
        DBConnection dbConnection = new MySqlConn(cfg);

        Map<String, DBConnection> connectionMap = new HashMap<>();
        connectionMap.put("testAlias", dbConnection);
        connectionList.set(connectionManager, connectionMap);

        singleTonInConnectionManager.set(connectionManager, connectionManager);

        setTableCmd = new SetTableCmd("use sakila", "testAlias");
        System.out.println("DB name before: " + connectionManager.getConnectionList().get("testAlias").getCfg().getDbName());
        setTableCmd.execute();
        Assert.assertEquals("sakila", connectionManager.getConnectionList().get("testAlias").getCfg().getDbName());
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
