package com.StrapleGroup.around.database.base;

/**
 * Created by Robert on 2014-12-25.
 */
public class LogInfo extends ModelBase {
    private long time;
    private String login;
    private String type;

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
