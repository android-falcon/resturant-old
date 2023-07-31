package restaurant.apps.falcons.flaconsrestaurant.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Salah on 8/6/2016.
 */

public class Constants {

    public static String ipAddress = "ipAddress";
    public static String userID = "userID";
    public static String settingsPassword = "falcon123";
    public static String availableColor = "availableColor";
    public static String usedColor = "usedColor";
    public static String finishSoonColor = "finishSoonColor";
    public static String reservedColor = "reservedColor";
    public static String lockedColor = "lockedColor";
    public static String language = "language";
    public static String posNO = "posNO";
    public static String storeNO = "storeNO";
    public static String deviceID = "deviceID";
    public static String deviceType = "deviceType";
    public static String sectionsByPos = "SectionsByPOS";
    public static String dateByPosNo = "DateByPosNo";
    public static String work_withTime = "work_withTime";
    public static String call_captin = "call_captin";

    public static int DEVICE_TYPE_DINE_IN = 0;
    public static int DEVICE_TYPE_TAKE_OUT = 1;

    public static SharedPreferences getSharedPrefs(Context context) {
        String sharedPref = "sharedPref";
        return context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
    }
}
