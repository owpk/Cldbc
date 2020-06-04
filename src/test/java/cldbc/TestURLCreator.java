package cldbc;

import cldbc.connection.DBConnection;
import cldbc.connection.MySqlConn;
import cldbc.connection.PostgresConn;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import cldbc.util.ConfigParams;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TestURLCreator {
    private ConfigParams cfg;
    private DBConnection dbConnection;
    private Field urlPrefix;
    private Method createUrl;

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

    @Before
    public void init() {
        cfg = createTestConfig();
    }

    @Test
    public void shouldReturnMySQLURLFormat() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        dbConnection = new MySqlConn(cfg);
        urlPrefix = DBConnection.class.getDeclaredField("urlPrefix");
        urlPrefix.setAccessible(true);
        String value = (String) urlPrefix.get(dbConnection);
        createUrl = DBConnection.class.getDeclaredMethod("createURL");
        createUrl.setAccessible(true);
        cfg.setParams("?params");
        Assert.assertEquals(value + "1:1/1?params", createUrl.invoke(dbConnection));
    }

    @Test
    public void shouldReturnPostgresURLFormat() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        dbConnection = new PostgresConn(cfg);
        urlPrefix = DBConnection.class.getDeclaredField("urlPrefix");
        urlPrefix.setAccessible(true);
        String value = (String) urlPrefix.get(dbConnection);
        createUrl = DBConnection.class.getDeclaredMethod("createURL");
        createUrl.setAccessible(true);
        cfg.setParams("?params");
        Assert.assertEquals(value + "1:1/1?params", createUrl.invoke(dbConnection));
    }

}
