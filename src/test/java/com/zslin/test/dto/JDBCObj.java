package com.zslin.test.dto;

/**
 * Created by zsl on 2019/4/1.
 */
public class JDBCObj {

    private String database;

    private String user;

    private String pwd;

    private String ip;

    private String url;

    public JDBCObj(String ip, String database, String user, String pwd) {
        this.ip = ip;
        this.database = database;
        this.user = user;
        this.pwd = pwd;
        this.url = "jdbc:mysql://"+ip+"/"+database;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
