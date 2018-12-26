package com.mks.sendstatlib.HttpsConnection;

public class Proxy {
    private String host;
    private String port;
    private String Username;
    private String Password;
    private int Timeout = 0;

    public String getHost() {
        return host;
    }

    public Proxy setHost(String host) {
        this.host = host;
        return this;
    }

    public String getPort() {
        return port;
    }

    public Proxy setPort(String port) {
        this.port = port;
        return this;
    }

    public String getUsername() {
        return Username;
    }

    public Proxy setUsername(String username) {
        Username = username;
        return this;
    }

    public String getPassword() {
        return Password;
    }

    public Proxy setPassword(String password) {
        Password = password;
        return this;
    }

    public int getTimeout() {
        return Timeout;
    }

    public Proxy setTimeout(int Timeout) {
        this.Timeout = Timeout;
        return this;
    }
}
