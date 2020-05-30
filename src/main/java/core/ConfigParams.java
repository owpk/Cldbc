package core;

import java.util.List;
import java.util.stream.Collectors;

public class ConfigParams {

    private String alias;
    private String vendor;
    private String dbName;
    private String userName;
    private String userPass;
    private String host;
    private String port;
    private List<String> paramList;
    private String params;


    public String showParams() {
        return "Database vendor: " + getVendor() + "\nConnection properties: " + host + ":" + port + "\nDatabase name: " + dbName +
                "\nUser: " + userName + "\nConfig parameters: \n" + paramList;
    }

    public void makeParams(List<String> list) {
        List<String> tempList = list.stream()
                .map(String::trim)
                .filter(s -> s.matches(".+=.+"))
                .collect(Collectors.toList());
        paramList = tempList;
        params = "?" +
                tempList.stream()
                        .map(p -> p = p.concat("&"))
                        .collect(Collectors.joining());
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
}
