package com.StrapleGroup.around.base;

public interface Constants {
    static final String REGISTER_ACTION = "com.StrapleGroup.gcm.REGISTER";
    static final String ADD_ACTION = "com.StrapleGroup.gcm.REQUEST";
    static final String ADD_RESPONSE = "com.StrapleGroup.gcm.FRIENDADD";
    static final String LOGIN_ACTION = "com.StrapleGroup.gcm.LOGIN";
    static final String REFRESH_ACTION = "com.StrapleGroup.gcm.LOCATION";
    static final String DELETE_ACTION = "com.StrapleGroup.gcm.DELETE";

    static final String DELETE_LOCAL_ACTION = "com.StrapleGroup.around.action.DELETE";
    static final String LOGIN_LOCAL_ACTION = "com.StrapleGroup.around.action.LOGIN";
    static final String REGISTER_LOCAL_ACTION = "com.StrapleGroup.around.action.REGISTER";
    static final String ADD_REQUEST_LOCAL_ACTION = "com.StrapleGroup.around.action.REQUEST";
    static final String LOCATION_ACTION = "com.StrapleGroup.around.action.LOCATION_INFO";
    static final String REFRESH_LOCAL_ACTION = "com.StrapleGroup.around.action.REFRESH";
    static final String MARKER_LOCAL_ACTION = "com.StrapleGroup.around.action.MARKER";
    static final String ADD_LOCAL_ACTION = "com.StrapleGroup.around.action.ADD";


    static final String KEY_LOGIN = "login";
    static final String KEY_PASS = "pass";
    static final String KEY_SERVER_PASS = "password";
    static final String KEY_ACTION = "action";
    static final String KEY_MESSAGE = "message";
    static final String SENDER_ID = "960206351442";
    static final String SERVER_ID = SENDER_ID + "@gcm.googleapis.com";
    static final String REG_ID = "registration_id";
    static final String PROPERTY_APP_VERSION = "appVersion";
    static final String LAT_SERVER = "x";
    static final String LNG_SERVER = "y";
    static final String COMPLETED = "completed";
    static final String INCOMPLETED = "incompleted";

    static final String USER_PREFS = "userLoginData";
    static final String LATLNG_PREFS = "latLngData";
    static final String GCM_PREFS = "gcmStorage";
}
