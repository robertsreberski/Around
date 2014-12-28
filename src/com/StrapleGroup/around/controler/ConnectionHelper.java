package com.StrapleGroup.around.controler;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.location.DetectedActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Created by Robert on 2014-12-21.
 */
public class ConnectionHelper implements Constants {
    static final String TEST_SERVER = "http://testrelease.straplegroup.com:8080/AroundServlet/ServletTest";
    static final String OFFICIAL_SERVER = "http://officialrelease.straplegroup.com/";
    private SharedPreferences pPrefs;
    private Context context;
    private ObjectMapper mapper = new ObjectMapper();


    public ConnectionHelper(Context context) {
        this.context = context;
        this.pPrefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
    }

    public boolean loginToApp(String aLogin, String aPass, Double aLat, Double aLng, int aActivity) {
        boolean bool = false;
        JSONObject pObject = new JSONObject();
        try {
            pObject.put(KEY_ACTION, LOGIN_SERVER_ACTION);
            pObject.put(KEY_LOGIN, aLogin);
            pObject.put(KEY_PASS, aPass);
            pObject.put(KEY_X, aLat);
            pObject.put(KEY_Y, aLng);
            pObject.put(KEY_STATUS, STATUS_ONLINE);
            pObject.put(KEY_ACTIVITY, aActivity);
            System.out.println(pObject.toString());
            // Where should request go?
            bool = restoreData(sendToServer(pObject));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("SERVER", "PROBLEM WITH SERVER");
            e.printStackTrace();
        }
        return bool;
    }

    public boolean updatePhoto(String aLogin, String aPass, String aPhoto) {
        boolean bool = false;
        try {
            JSONObject pObject = new JSONObject();
            pObject.put(KEY_ACTION, PHOTO_UPDATE_SERVER_ACTION);
            pObject.put(KEY_LOGIN, aLogin);
            pObject.put(KEY_PASS, aPass);
            pObject.put(KEY_PHOTO, aPhoto);
            // Where should request go?
            bool = sendToServer(pObject).getBoolean(KEY_VALID);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("SERVER", "PROBLEM WITH SERVER");
            e.printStackTrace();
        }
        return bool;
    }

    public boolean registerToApp(String aLogin, String aPass, Double aLat, Double aLng, String aPhoto, int aActivity) {
        boolean bool = false;
        JSONObject pObject = new JSONObject();
        try {
            pObject.put(KEY_ACTION, REGISTER_SERVER_ACTION);
            pObject.put(KEY_LOGIN, aLogin);
            pObject.put(KEY_PASS, aPass);
            pObject.put(KEY_X, aLat);
            pObject.put(KEY_Y, aLng);
            pObject.put(KEY_STATUS, STATUS_ONLINE);
            pObject.put(KEY_ACTIVITY, aActivity);
            pObject.put(KEY_PHOTO, aPhoto);
            // Where should request go?
            bool = sendToServer(pObject).getBoolean(KEY_VALID);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("SERVER", "PROBLEM WITH SERVER");
            e.printStackTrace();
        }
        return bool;
    }

    public boolean sendAddRequest(String aLogin, String aPass, String aFriendLogin) {
        JSONObject pObject = new JSONObject();
        boolean bool = false;
        try {
            pObject.put(KEY_ACTION, ADD_SERVER_ACTION);
            pObject.put(KEY_LOGIN, aLogin);
            pObject.put(KEY_PASS, aPass);
            pObject.put(KEY_FRIEND, aFriendLogin);
            bool = sendToServer(pObject).getBoolean(KEY_VALID);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("SERVER", "PROBLEM WITH SERVER");
            e.printStackTrace();
        }
        return bool;
    }

    public boolean sendDeleteRequest(String aLogin, String aPass, String aFriendLogin) {
        JSONObject pObject = new JSONObject();
        boolean bool = false;
        try {
            pObject.put(KEY_ACTION, DELETE_SERVER_ACTION);
            pObject.put(KEY_LOGIN, aLogin);
            pObject.put(KEY_PASS, aPass);
            pObject.put(KEY_FRIEND, aFriendLogin);
            bool = sendToServer(pObject).getBoolean(KEY_VALID);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("SERVER", "PROBLEM WITH SERVER");
            e.printStackTrace();
        }
        return bool;
    }

    public boolean updatePhotoRequest(String aLogin, String aPass) {
        JSONObject pObject = new JSONObject();
        boolean bool = false;
        try {
            pObject.put(KEY_ACTION, DOWNLOAD_PHOTO_ACTION);
            pObject.put(KEY_LOGIN, aLogin);
            pObject.put(KEY_PASS, aPass);
            JSONObject pResponse = sendToServer(pObject);
            if (pResponse.getBoolean(KEY_VALID)) {
                bool = updatePhotoToDB(pResponse.getJSONArray(KEY_PHOTO_LIST));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("SERVER", "PROBLEM WITH SERVER");
            e.printStackTrace();
        }
        return bool;
    }

    public boolean sendAddResponse(String aLogin, String aPass, String aFriendLogin, boolean aResponse) {
        JSONObject pObject = new JSONObject();
        boolean bool = false;
        try {
            pObject.put(KEY_ACTION, RESPONSE_REQUEST_SERVER_ACTION);
            pObject.put(KEY_LOGIN, aLogin);
            pObject.put(KEY_PASS, aPass);
            pObject.put(KEY_FRIEND, aFriendLogin);
            pObject.put(KEY_VALID, aResponse);
            JSONObject pJsonResponse = sendToServer(pObject);
            if (pJsonResponse.getBoolean(KEY_VALID)) {
                FriendsInfo pFriend = new FriendsInfo();
                DataManagerImpl pDataManager = new DataManagerImpl(context);
                pFriend.setLoginFriend(aLogin);
                pFriend.setXFriend(pJsonResponse.getDouble(KEY_X));
                pFriend.setYFriend(pJsonResponse.getDouble(KEY_Y));
                pFriend.setStatus(pJsonResponse.getString(KEY_STATUS));
                pFriend.setActivities(pJsonResponse.getInt(KEY_ACTIVITY));
                pDataManager.updateFriendInfo(pFriend);
                bool = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("SERVER", "PROBLEM WITH SERVER");
            e.printStackTrace();
        }
        return bool;
    }

    public JSONObject updateToApp(String aLogin, String aPass, Double aLat, Double aLng, int aActivity, String aStatus) {
        JSONObject pObject = new JSONObject();
        JSONObject pJsonFinal = null;
        try {
            pObject.put(KEY_ACTION, UPDATE_SERVER_ACTION);
            pObject.put(KEY_LOGIN, aLogin);
            pObject.put(KEY_PASS, aPass);
            pObject.put(KEY_X, aLat);
            pObject.put(KEY_Y, aLng);
            pObject.put(KEY_STATUS, aStatus);
            pObject.put(KEY_ACTIVITY, aActivity);
            // Where should request go?
            JSONObject pJsonResponse = sendToServer(pObject);
            if (pJsonResponse.getBoolean(KEY_VALID))
                pJsonFinal = pJsonResponse;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("SERVER", "PROBLEM WITH SERVER");
            e.printStackTrace();
        }
        return pJsonFinal;
    }

    private boolean updatePhotoToDB(JSONArray aPhotoArray) throws JSONException {
        for (int i = 0; i < aPhotoArray.length(); i++) {
            JSONObject pJsonPhoto = aPhotoArray.getJSONObject(i);
            DataManagerImpl pDataManager = new DataManagerImpl(context);
            FriendsInfo pFriend = new FriendsInfo();
            pFriend.setLoginFriend(pJsonPhoto.getString(KEY_LOGIN));
            pFriend.setProfilePhoto(Base64.decode(pJsonPhoto.getString(KEY_PHOTO), 0));
            pDataManager.updatePhoto(pFriend);
        }
        return true;
    }

    private boolean restoreData(JSONObject aObject) throws JSONException {
        boolean pBool = false;
        if (aObject.getBoolean(KEY_VALID)) {
            pPrefs.edit().putString(KEY_PHOTO, aObject.getString(KEY_PHOTO)).commit();
            DataManagerImpl pDataManager = new DataManagerImpl(context);
            JSONArray pJsonArray = aObject.getJSONArray(KEY_FRIEND_LIST);
            if (pJsonArray != null) {
                for (int i = 0; i < pJsonArray.length(); i++) {
                    JSONObject pJsonFriend = pJsonArray.getJSONObject(i);
                    FriendsInfo pFriend = new FriendsInfo();
                    pFriend.setLoginFriend(pJsonFriend.getString(KEY_LOGIN));
                    pFriend.setXFriend(pJsonFriend.getDouble(KEY_X));
                    pFriend.setYFriend(pJsonFriend.getDouble(KEY_Y));
                    pFriend.setActivities(pJsonFriend.getInt(KEY_ACTIVITY));
                    pFriend.setStatus(pJsonFriend.getString(KEY_STATUS));
                    pFriend.setProfilePhoto(Base64.decode(pJsonFriend.getString(KEY_PHOTO), 0));
                    pDataManager.saveFriendInfo(pFriend);
                }
            }
            pBool = true;
        } else {
            pBool = false;
        }
        return pBool;
    }


    private JSONObject sendToServer(JSONObject aObject) throws IOException, JSONException {
        URL pUrl = new URL(TEST_SERVER);
        URLConnection pConnection = pUrl.openConnection();
        pConnection.setDoOutput(true);
        OutputStreamWriter out = new OutputStreamWriter(pConnection.getOutputStream());
        out.write(aObject.toString());
        out.close();
        Map<String, Object> jsonMap = mapper.readValue(pConnection.getInputStream(), Map.class);
        JSONObject jsonObject = new JSONObject(jsonMap);
        Log.e("JSON", jsonObject.toString());
        return jsonObject;
    }
}
