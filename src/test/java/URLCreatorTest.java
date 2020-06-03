import connection.DBConnection;
import connection.MySqlConn;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import util.ConfigParams;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class URLCreatorTest {
    private ConfigParams cfg;
    private DBConnection dbConnection;

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
        Field field = DBConnection.class.getDeclaredField("urlPrefix");
        field.setAccessible(true);
        String value = (String) field.get(dbConnection);
        Method m = DBConnection.class.getDeclaredMethod("createURL");
        m.setAccessible(true);
        cfg.setParams("?params");
        System.out.println(dbConnection.getCfg().showParams());
        Assert.assertEquals(value + "1:1/1?params", m.invoke(dbConnection));
    }

}
