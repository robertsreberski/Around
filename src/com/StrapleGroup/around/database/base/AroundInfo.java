package com.StrapleGroup.around.database.base;

/**
 * Created by Robert on 2014-12-29.
 */
public class AroundInfo extends ModelBase {
    private String login;
    private String distance;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
