package com.StrapleGroup.around.database.base;

/**
 * Created by Robert on 2014-12-29.
 */
public class AroundInfo extends ModelBase {
    private String login;
    private String distance;
    private double x;
    private double y;

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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
