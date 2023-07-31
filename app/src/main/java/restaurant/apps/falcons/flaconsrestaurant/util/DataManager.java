package restaurant.apps.falcons.flaconsrestaurant.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import restaurant.apps.falcons.flaconsrestaurant.R;
import restaurant.apps.falcons.flaconsrestaurant.models.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static restaurant.apps.falcons.flaconsrestaurant.util.Constants.DEVICE_TYPE_DINE_IN;
import static restaurant.apps.falcons.flaconsrestaurant.util.Constants.DEVICE_TYPE_TAKE_OUT;

/**
 * Created by pure_ on 30/07/2016.
 */

public class DataManager {
    private static List<Table> tables = new ArrayList<>();
    private static LinkedHashMap<String, Item> items = new LinkedHashMap<>();
    private static LinkedHashMap<String, Category> categories = new LinkedHashMap<>();
    private static LinkedHashMap<String, Question> questions = new LinkedHashMap<>();
    private static List<RefundReason> refundReasons = new ArrayList<>();
    private static LinkedHashMap<String, String> sections = new LinkedHashMap<>();
    private static LinkedHashMap<String, User> users = new LinkedHashMap<>();
    private static User[] currentUser = {null};
    private static HashMap<String, String> registers = new HashMap<>();

    private final static String mySdf = "dd/MM/yyyy HH:mm";


    public static HashMap<String, String> getRegisters() {
        return registers;
    }

    public static boolean needReload() {
        return tables.size() == 0 && items.size() == 0;
    }

    public static void setCurrentUser(User user) {
        currentUser[0] = user;
    }
    private static String posNo;
    private static String dateByPosNo;

    public static User getCurrentUser(Context context) {
        if (currentUser[0] != null)
            return currentUser[0];
        SharedPreferences sp = Constants.getSharedPrefs(context);
        String userID = sp.getString(Constants.userID, "");
        return users.get(userID);
    }

    public static List<Table> getAllTables() {
        return tables;
    }

    public static boolean pingHost(String host, int port, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.close();
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }

    private static String getVHFID(boolean takeAway, Context context) {

        SharedPreferences sp = Constants.getSharedPrefs(context);
        String posNo = sp.getString(Constants.posNO, "1");
        String query;

        dateByPosNo = sp.getString(Constants.dateByPosNo, "0");

        if ((dateByPosNo.equals("1")) && (posNo != null))
            query = "ID=3&type=12&DB=1963-1972-1973-1976-&data=1&TakeAway=" +
                (takeAway ? "1" : "0")+"&POS_NO="+posNo;
        else
            query = "ID=3&type=12&DB=1963-1972-1973-1976-&data=1&TakeAway=" +
                    (takeAway ? "1" : "0");

        return ConnectionManager.getPHPData(query);
    }

    public static boolean clearTableItems(String tableId) {
        String query = "DELETE FROM WAITERDETAILS WHERE TABLE_ID=%s";
        query = String.format(Locale.ENGLISH, query, tableId);
        return DoQuery(query).equals("Successful");
    }

    static void clearTableItems(Context context, String tableId) {
        String query = "ID=2&type=801&DB=1963-1972-1973-1976-&data=1&TABLE_ID=%s&POS_NO=%s";
        query = String.format(query, tableId, getDeviceID(context));
        System.out.println("dismiss result : " + ConnectionManager.Query(query));
    }

    private static String getTableStatus(Table table) {
        String query = "ID=1&type=52&DB=1963-1972-1973-1976-&data=1&TBLNO=" + table.getId();
        return ConnectionManager.getPHPData(query);
    }

    private static String getTableSplitStatus(Table table) {
        String query = "ID=1&type=62&DB=1963-1972-1973-1976-&data=1&TBLNO=" + table.getId();
        return ConnectionManager.getPHPData(query);
    }

    private static void getTableHeader(Table table) {
        String query = String.
                format("ID=1&type=53&DB=1963-1972-1973-1976-&data=1&VHFNO=%s&SECNO=%s&TBLNO=%s"
                        , table.getOrderID()
                        , table.getSection()
                        , table.getId());
        try {
            JSONArray array = new JSONArray(ConnectionManager.getPHPData(query));
            if (array.length() > 0) {
                JSONObject object = array.getJSONObject(0);
                String discount = object.optString("DISCOUNT");
                if (discount.length() == 0) {
                    discount = "0";
                }
                System.out.println(discount + " <- discount");
                table.setDiscount(Float.valueOf(discount));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static List<String> checkIfWaiterIsCalled(Context context) {
        String query = String.format(Locale.ENGLISH, "ID=1&type=54&DB=1963-1972-1973-1976-&data=1&POS_NO=" + getDeviceID(context));

        List<String> tables = new ArrayList<>();
        try {
            String response = ConnectionManager.getPHPData(query);
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String tableId = object.getString("TABLE_ID");
                tables.add(tableId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return tables;
    }

    static List<Table> checkForCheckOuts(Context context) {
        String query = String.format(Locale.ENGLISH, "ID=1&type=55&DB=1963-1972-1973-1976-&data=1&POS_NO=" + getDeviceID(context));

        List<Table> tables = new ArrayList<>();
        try {
            String result = ConnectionManager.getPHPData(query);
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                Table table = new Table();

                String tableId = object.getString("TABLE_ID");
                String note = object.getString("PAYMENT_METHOD");

                table.setId(tableId);
                table.setNote(note);

                tables.add(table);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return tables;
    }

    static List<Table> checkForOrders(Context context) {
        String query = String.format("ID=1&type=56&DB=1963-1972-1973-1976-&data=1&POS_NO=" + getDeviceID(context));

        List<Table> tables = new ArrayList<>();
        try {
            String result = ConnectionManager.getPHPData(query);
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                Table table = new Table();

                String tableId = object.getString("TABLE_ID");

                table.setId(tableId);
                table.setItems(getTableOrder(tableId));

                tables.add(table);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return tables;
    }

    public static List<Item> getTableOrder(String tableId) {
        String query = String.format("ID=1&type=57&DB=1963-1972-1973-1976-&data=1&TABLE_ID=" + tableId);

        List<Item> items = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(ConnectionManager.getPHPData(query));
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                Item item = new Item();

                String itemId = object.getString("ITEM_ID");
                String qty = object.getString("ITEM_QTY");
                String seatNumber = object.getString("SEAT_NO");
                String itemNotes = object.getString("ITEM_NOTES");

                item.setId(itemId);
                item.setQty(Integer.parseInt(qty));
                item.setSeatNumber(Integer.parseInt(seatNumber));
                item.setNotes(itemNotes);

                items.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return items;
    }

    public static List<String> getDismissedTables() {
        String query = "ID=1&type=58&DB=1963-1972-1973-1976-&data=1";

        List<String> tablesIds = new ArrayList<>();
        try {
            String result = ConnectionManager.getPHPData(query);
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                String tableId = object.getString("TABLE_ID");
                String request = object.getString("REQUEST");
                int id = (int) (Integer.parseInt(tableId) * Math.pow(10, (Integer.parseInt(request) + 1)));
                tablesIds.add(id + "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return tablesIds;
    }

    public static List<String> getUnDismissedTablesIds() {
        String query = "ID=1&type=60&DB=1963-1972-1973-1976-&data=1";

        List<String> tablesIds = new ArrayList<>();
        try {
            String result = ConnectionManager.getPHPData(query);
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String tableId = object.getString("TABLE_ID");
                String request = object.getString("REQUEST");
                int id = (int) (Integer.parseInt(tableId) * Math.pow(10, (Integer.parseInt(request) + 1)));
                tablesIds.add(id + "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return tablesIds;
    }

    public static int getTableStatus(String tableId) {
        String query = "ID=1&type=61&DB=1963-1972-1973-1976-&data=1&TABLE_ID='%s'";
        query = String.format(Locale.ENGLISH, query, tableId);

        int status;
        try {
            String result = ConnectionManager.getPHPData(query);
            JSONArray array = new JSONArray(result);
            JSONObject object = array.getJSONObject(0);
            status = object.optInt("TBLSTATUS");
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }

        return status;
    }

    static void sendNotification(Context context, String tableId) {
        String query = "ID=2&type=800&DB=1963-1972-1973-1976-&data=1&TABLE_ID=%s&POS_NO=%s";
        query = String.format(query, tableId, getDeviceID(context));
        System.out.println("send notification result : " + ConnectionManager.Query(query));
    }

    static void dismissNotification(Context context, String tableId) {
        String query = "ID=2&type=801&DB=1963-1972-1973-1976-&data=1&TABLE_ID=%s&POS_NO=%s";
        query = String.format(query, tableId, getDeviceID(context));
        System.out.println("dismiss result : " + ConnectionManager.Query(query));
    }

    public static void lockTable(Table table, Context context) {
        Log.e("lockTable",""+table.getId());
        String query = "ID=2&type=777&DB=1963-1972-1973-1976-&data=1&TBLNO=%s&PCID=%s";
        query = String.format(query, table.getId(), getDeviceID(context));
//        System.out.println("lock result : " + ConnectionManager.Query(query));
        Log.e("lockTable",""+ConnectionManager.Query(query));
    }

    public static void unlockTable(Table table) {
        Log.e("unlockTable",""+table.getId());
        String query = "ID=2&type=778&DB=1963-1972-1973-1976-&data=1&TBLNO=%s";
        query = String.format(query, table.getId());
        System.out.println("unlock result : " + ConnectionManager.Query(query));
    }

    public static LinkedHashMap<String, User> getUsers() {
        return users;
    }

    private static void sendUDPTablesUpdate(Context context, Table table) {
        try {
            String messageStr = "TABLE_NEW_ORDER:" + table.getId() + ":" + table.getSection();
            int server_port = 2070;
            try {
                server_port = Integer.parseInt(getRegisters().get("UDP_MSG_PORT"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            DatagramSocket s;

            SharedPreferences sp = Constants.getSharedPrefs(context);
            String server = sp.getString(Constants.ipAddress, "");

            s = new DatagramSocket();
            InetAddress local = InetAddress.getByName(server);
            int msg_length = messageStr.length();
            byte[] message = messageStr.getBytes();
            DatagramPacket p = new DatagramPacket(message, msg_length, local, server_port);
            s.send(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printInvoice(Context context, Table table) {
        try {
            String messageStr = "PRINT_RECEIPT:0:" + table.getId() + ":" + table.getSection();
            int server_port = 2070;
            try {
                server_port = Integer.parseInt(getRegisters().get("UDP_MSG_PORT"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            DatagramSocket s;

            SharedPreferences sp = Constants.getSharedPrefs(context);
            String server = sp.getString(Constants.ipAddress, "");

            s = new DatagramSocket();
            InetAddress local = InetAddress.getByName(server);
            int msg_length = messageStr.length();
            byte[] message = messageStr.getBytes();
            DatagramPacket p = new DatagramPacket(message, msg_length, local, server_port);
            s.send(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean sendingUdp = false;


    public static void sendUDP(Table table, Context context) {
        try {
            if (sendingUdp) {
                return;
            }
            sendingUdp = true;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    sendingUdp = false;
                }
            }, 6000);
            String messageStr = String.format("PRINT:%s:%s:%s", table.getOrderID(), table.getSection(), table.getId());
            int server_port = 2070;
            try {
                server_port = Integer.parseInt(getRegisters().get("UDP_MSG_PORT"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            DatagramSocket s;

            SharedPreferences sp = Constants.getSharedPrefs(context);
            String server = sp.getString(Constants.ipAddress, "");

            s = new DatagramSocket();
            InetAddress local = InetAddress.getByName(server);
            int msg_length = messageStr.length();
            byte[] message = messageStr.getBytes();
            DatagramPacket p = new DatagramPacket(message, msg_length, local, server_port);
            s.send(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getTableVHFNO(Table table, Context context) {

        SharedPreferences sp = Constants.getSharedPrefs(context);
        //String server = sp.getString(Constants.ipAddress, "");
        posNo =  sp.getString(Constants.posNO, "");
        String query;
        dateByPosNo = sp.getString(Constants.dateByPosNo, "0");;
        if (dateByPosNo.equals("1"))
        {
            query = "ID=1&type=13&DB=1963-1972-1973-1976-&data=1&TSCNO=%s&TBLNO=%s&POS_NO="+posNo;
        }
        else
        {
            query = "ID=1&type=13&DB=1963-1972-1973-1976-&data=1&TSCNO=%s&TBLNO=%s";
        }

        query = String.format(Locale.ENGLISH, query, table.getSection(), table.getId());
        String result = ConnectionManager.getPHPData(query);
        if (result.length() == 0) return;

        try {
            JSONArray array = new JSONArray(result);
            if (array.length() > 0) {
                JSONObject object = array.getJSONObject(0);
                String vhfno = object.optString("VHFNo");
                String seatCount = object.getString("NOOFSEATS");
                if (seatCount.length() == 0) seatCount = "0";
                table.setOrderID(vhfno);
                table.setSeatCount(Integer.valueOf(seatCount));
                String user = object.getString("STARTUSERNO");
                table.setUser(user);
                System.out.println(result);
                getTableTransKind(table);
            }
            System.out.println(result);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void getTableTransKind(Table table) {
        String query = "ID=1&type=10&DB=1963-1972-1973-1976-&data=1";
        query = String.format(Locale.ENGLISH, query, table.getSection(), table.getId());
        String result = ConnectionManager.getPHPData(query);
        if (result.length() == 0) return;

        try {
            JSONArray array = new JSONArray(result);
            if (array.length() > 0) {
                JSONObject object = array.getJSONObject(0);
                String transNo = object.optString("TRANSNO");
                String transKind = object.optString("TRANSKIND");
                table.setTransNo(transNo);
                table.setTransKind(transKind);
                System.out.println(result);
            }
            System.out.println(result);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getTableData(Table table, Context context) {
        table.getItems().clear();
        table.getRefundItems().clear();

        getTableVHFNO(table, context);
        getTableHeader(table);

        String query = "ID=1&type=26&DB=1963-1972-1973-1976-&data=1&VHFNO=" + table.getOrderID();
        String data = ConnectionManager.getPHPData(query);
        try {
            JSONArray array = new JSONArray(data);
            SharedPreferences sp = Constants.getSharedPrefs(context);
            String language = sp.getString(Constants.language, "en");
            for (int x = 0; x < array.length(); x++) {
                JSONObject object = array.getJSONObject(x);
                String itemID = object.getString("ITEMOCODE");
                String Qty = object.getString("IOQTY");
                String Price = object.getString("PRICE");
                //String Date = object.getString("VHFDATE");
                //String Time = object.getString("VHFTIME");
                //String User = object.getString("USERNO");
                String Note = object.getString("ITEMREMARK");
                String isForcedQ = object.getString("ISFORCEQUESTION");

                String name = object.getString("ITEMONAMEA");
                String name2 = object.getString("ITEMNAMEE");

                String ITEM_START_TIME = object.getString("ITEM_START_TIME");
                String ITEM_END_TIME = object.getString("ITEM_END_TIME");

                int ITEMSOLDBYHOUR = object.getInt("ITEMSOLDBYHOUR");
                int STOP_ITEM_TIME = object.getInt("STOP_ITEM_TIME");

                if (isForcedQ.equals("1")) {
                    if (table.getItems().size() == 0) {
                        continue;
                    }
                    Item item = table.getItems().get(table.getItems().size() - 1);
                    for (Question question : item.getQuestions()) {
                        if (question.getType().equals("2")) {
                            for (Answer answer : question.getAnswers()) {
                                if (answer.getItem().getId().equals(itemID)) {
                                    item.getAnswers().add(answer);
                                    break;
                                }
                            }
                        }
                    }
                    continue;
                }

                Item item = getItemCopy(getItems(null).get(itemID));
                item.setQty(Float.parseFloat(Qty));
                item.setOldQty(Float.parseFloat(Qty));
                item.setPrice(Float.parseFloat(Price));
                item.setNote(Note);
                item.setOld(true);
                item.setName(language.equals("en") ? name2 : name);
                item.setName2(language.equals("ar") ? name2 : name);

                try
                {
                    if (object.getInt("ITEMSOLDBYHOUR") == 1)
                    {
                        item.setTimeItem(true);
                        try
                        {
                            if (object.getInt("STOP_ITEM_TIME") == 1)
                                item.setIsFinishedTime(1);

                            else
                                item.setIsFinishedTime(0);
                        }catch (Exception e)
                        {
                            item.setIsFinishedTime(0);
                            item.setIsFinishedTime(0);
                        }

                        try {
                            Date startTime = new SimpleDateFormat(mySdf).parse(object.getString("ITEM_START_TIME"));
                            Date endTime = new SimpleDateFormat(mySdf).parse(object.getString("ITEM_END_TIME"));
                            Date endDate = new Date();

                            String dateString = new SimpleDateFormat(mySdf).format(endDate);
                            try {
                                endDate = new SimpleDateFormat(mySdf).parse(dateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            long difference = 1;
                            if (item.getIsFinishedTime() == 1)
                                difference = endTime.getTime() - startTime.getTime();
                            else
                                difference = endDate.getTime() - startTime.getTime();

                            long seconds = difference / 1000;

                            long minutes = seconds / 60;

                            if (minutes < 1)
                                minutes = 1;

                            item.setOpenTime(startTime);
                            item.setEndTime(endTime);

                            if (item.getIsFinishedTime() == 0)
                                item.setQty(minutes);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }

                    else
                        item.setTimeItem(false);
                }catch (Exception e)
                {
                    item.setTimeItem(false);
                }


                table.getItems().add(item);

            }
//            serializeAndSaveTable(table, context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public static void serializeAndSaveTable(Table table, Context context) {
//        SharedPreferences sp = Constants.getSharedPrefs(context);
//        SharedPreferences.Editor prefsEditor = sp.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(table);
//        prefsEditor.putString("SerializableObject", json);
//        prefsEditor.commit();
//    }
//
//    public static Table getSerializedTable(Context context) {
//        SharedPreferences sp = Constants.getSharedPrefs(context);
//        Gson gson = new Gson();
//        String json = sp.getString("SerializableObject", "");
//        Table table = gson.fromJson(json, Table.class);
//        return table;
//    }

    private static String toISO8859(String str) {
        if (true) {
            try {
                return URLEncoder.encode(str, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
                return str;
            }
        }
        HashMap<String, String> isoMap = new HashMap<>();
        if (isoMap.size() == 0) {
            isoMap.put("ہ", "À");
            isoMap.put("ء", "Â");
            isoMap.put("آ", "Â");
            isoMap.put("أ", "Ã");
            isoMap.put("ؤ", "Ä");
            isoMap.put("إ", "Å");
            isoMap.put("ئ", "Æ");
            isoMap.put("ا", "Ç");
            isoMap.put("ب", "È");
            isoMap.put("ة", "É");
            isoMap.put("ت", "Ê");
            isoMap.put("ث", "Ë");
            isoMap.put("ج", "Ì");
            isoMap.put("ح", "Í");
            isoMap.put("خ", "Î");
            isoMap.put("د", "Ï");
            isoMap.put("ذ", "Ð");
            isoMap.put("ر", "Ñ");
            isoMap.put("ز", "Ò");
            isoMap.put("س", "Ó");
            isoMap.put("ش", "Ô");
            isoMap.put("ص", "Õ");
            isoMap.put("ض", "Ö");
            isoMap.put("ط", "Ø");
            isoMap.put("ظ", "Ù");
            isoMap.put("ع", "Ú");
            isoMap.put("غ", "Û");
            isoMap.put("ف", "Ý");
            isoMap.put("ق", "Þ");
            isoMap.put("ك", "ß");
            isoMap.put("ـ", "Ü");
            isoMap.put("ل", "á");
            isoMap.put("م", "ã");
            isoMap.put("ن", "ä");
            isoMap.put("ه", "å");
            isoMap.put("و", "æ");
            isoMap.put("ى", "ì");
            isoMap.put("ي", "í");
        }

        for (String s : isoMap.keySet()) {
            str = str.replace(s, isoMap.get(s));
        }
        return str;
    }

    public static String getDeviceID(Context context) {
        SharedPreferences sp = Constants.getSharedPrefs(context);
        String customID = sp.getString("deviceID", "");

        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (!customID.equals("")) {
            android_id = customID;
        }
        return android_id;
    }

    public static boolean isTableLocked(final Activity context, Table table, boolean checkForEmpty) {
        if (DataManager.getRegisters().size() == 0) {
            return true;
        }
        if (DataManager.getRegisters().get("STOP_OPEN_TABLE").equals("1")) {
            String status = DataManager.getTableStatus(table);
            try {
                JSONArray array = new JSONArray(status);
                if (array.length() > 0) {
                    final String openedBy = array.getJSONObject(0).optString("PCID");
                    if (!openedBy.equals(DataManager.getDeviceID(context))) {
                        context.runOnUiThread(new TimerTask() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Table is locked for " + openedBy, Toast.LENGTH_LONG).show();
                            }
                        });
                        return true;
                    }
                } else if ( (array.length() <= 0) && (checkForEmpty) )
                {
                    context.runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            Toast.makeText(context, R.string.cleanedTable, Toast.LENGTH_LONG).show();
                        }
                    });
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String splitStatus = DataManager.getTableSplitStatus(table);
        try {
            JSONArray array = new JSONArray(splitStatus);

            if( array.length() > 0) {
                JSONObject object1 = array.getJSONObject(0);
                String ISSPLITED = object1.getString("ISSPLITED");
                if (ISSPLITED.equals("2"))
                {
                    context.runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            Toast.makeText(context, R.string.splitedError, Toast.LENGTH_LONG).show();
                        }
                    });
                    return true;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean saveTable(Table table, final Context context) {
//        Table tableX = new Table();
//        tableX.setId(table.getId());
//        tableX.setSection(table.getSection());
//        tableX.setOrderID("");
//        getTableData(tableX, context);
//        if (!tableX.getOrderID().equals(table.getOrderID())) {
//            ((Activity) context).runOnUiThread(new TimerTask() {
//                @Override
//                public void run() {
//                    Toast.makeText(context, "Error : Something is wrong\nplease restart the invoice"
//                            , Toast.LENGTH_LONG).show();
//                }
//            });
//
//            return false;
//            table.setOrderID(tableX.getOrderID());
//        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);

        boolean isTakeOut = getDeviceType(context) == DEVICE_TYPE_TAKE_OUT;

        JSONArray jsonArray;
        try {
            boolean update = table.getOrderID().length() > 0;
           // 05 - 12 - 2020

                String jsonData = getVHFID(isTakeOut, context);
                jsonArray = new JSONArray(jsonData);
                String transNo = jsonArray.getJSONObject(0).getString("TRANSNO");
                String transKind = jsonArray.getJSONObject(0).getString("TRANSKIND");
                String vhfNo = jsonArray.getJSONObject(1).getString("VHFNO");
                String vhfNo_temp = jsonArray.getJSONObject(2).getString("VHFN_TEMP");
                String date = jsonArray.getJSONObject(3).getString("TODAYPOS");
                if (vhfNo_temp.length() == 0) {
                    vhfNo_temp = "1";
                }

            if (!update) {
                table.setTempVHFNO(isTakeOut ? vhfNo : vhfNo_temp);
                table.setOrderID(vhfNo_temp);
                table.setTransKind(transKind);
                table.setTransNo(transNo);
            }

            try {
                table.setDate(simpleDateTimeFormat.parse(date + " " + simpleTimeFormat.format(new Date())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
//            }

            if (table.getOrderID() == null || table.getOrderID().length() == 0) {
                return false;
            }
            if (table.getTransKind() == null || table.getTransKind().length() == 0) {
                return false;
            }
            if (table.getTransNo() == null || table.getTransNo().length() == 0) {
                return false;
            }

            SharedPreferences sp = Constants.getSharedPrefs(context);
            String posNo = sp.getString(Constants.posNO, "1");
            dateByPosNo = sp.getString(Constants.dateByPosNo, "0");
            if (posNo.length() == 0) {
                posNo = "0";
            }

            JSONObject object = new JSONObject();
            object.put("TRANSNO", table.getTransNo());
            object.put("TRANSKIND", table.getTransKind());
            object.put("VHFNO", table.getOrderID());
            object.put("POSNO", posNo);
            object.put("VHFDATE", simpleDateFormat.format(table.getDate()));
            object.put("VHFTIME", simpleTimeFormat.format(table.getDate()));
            object.put("USERNO", table.getUser());

            if (7 == 7) {
                object.put("VTOTAL", String.format(Locale.ENGLISH, "%.3f", table.getTotal() + table.getTax()));
            }else
            {
                object.put("VTOTAL", String.format(Locale.ENGLISH, "%.3f", table.getTotal()));
            }
            object.put("DISCOUNT", table.getDiscount());
            object.put("VTOTALF", String.format(Locale.ENGLISH, "%.3f", table.getTax()));
            object.put("AMTPAID", "-1");
            object.put("NetTotL", String.format(Locale.ENGLISH, "%.3f", table.getTotal() + table.getTax()));
            object.put("REMARK", toISO8859(table.getNote()));
            object.put("HINTS", "");
            object.put("HOWMANY", table.getSeatCount());
            object.put("VHFS", "0.000");
            object.put("VHFI", table.getOrderID());
            object.put("CUSTOMERNO", "");
            object.put("CREDITCARDNO", "");
            object.put("TRANSTYPE", "0");
            object.put("TRANSTYPESTR", "0.000000");
            object.put("ORNo", "");
            object.put("OPOSNo", "2");
            object.put("CreditAmt", "0");
            object.put("SalesManNo", "");
            object.put("DISCPERC", "");
            object.put("DISCNO", "0");
            object.put("SECTIONNO", table.getSection());
            object.put("ISORDER", "0");
            object.put("ORDERDATE", simpleDateFormat.format(table.getDate()));
            object.put("ORDERTIME", "");
            object.put("ORDERNO", "0");
            object.put("PAPERVHFNO", "");
            object.put("TBLREMARK", toISO8859(table.getNote()));
            object.put("TBLNO", table.getId());
            object.put("SECNO", table.getSection());
            object.put("POSNOFORTEMPSERIAL", "0");
            object.put("ISTAXEXMPT", "0");

            String header = "[" + object.toString() + "]";

            JSONArray array = new JSONArray();
            List<Item> itemList = new ArrayList<>();
            for (Item item : table.getItems()) {
                itemList.add(item);
                for (Answer answer : item.getAnswers()) {
                    if (answer.getItem() != null) {
                        answer.getItem().setQty(item.getQty());
                        if (answer.getItem().useNewPrice()) {
                            answer.getItem().setPrice(answer.getItem().getNewPrice());
                        }
                        answer.getItem().setForceQ(true);
                        answer.getItem().setOld(item.isOld());
                        itemList.add(answer.getItem());
                    }
                }
            }

            boolean calcBeforeTax = DataManager.getRegisters().get("TAXCALCKIND").equals("1");

            String language = sp.getString(Constants.language, "en");

            for (Item item : itemList) {
                float price = item.getPrice();
                if (!calcBeforeTax) {
                    price = price / (1 + item.getTax() / 100f);
                }

                float itemTotal = item.getQty() * price;

                object = new JSONObject();
                object.put("TRANSNO", table.getTransNo());
                object.put("VHFNO", table.getOrderID());
                object.put("TRANSKIND", table.getTransKind());
                object.put("VSERIAL", itemList.indexOf(item)+1);//05-12-2020
                object.put("POSNO", posNo);
                object.put("IOQTY", item.getQty());
                object.put("IOBONUS", "0");
                object.put("PRICE", String.format(Locale.ENGLISH, "%.3f", item.getPrice()));
                object.put("DISCOUNT", item.getDiscount());

                if (7 == 7) {
                    object.put("TOTAL", String.format(Locale.ENGLISH, "%.3f", itemTotal+itemTotal * item.getTax() / 100f));
                    object.put("NETT", String.format(Locale.ENGLISH, "%.3f", itemTotal+itemTotal * item.getTax() / 100f));
                }
                else
                {
                    object.put("TOTAL", String.format(Locale.ENGLISH, "%.3f", itemTotal));
                    object.put("NETT", String.format(Locale.ENGLISH, "%.3f", itemTotal));
                }
                object.put("CUSTOMERNO", "");
                object.put("VHFDATE", simpleDateFormat.format(table.getDate()));
                object.put("VHFTIME", simpleTimeFormat.format(table.getDate()));
                object.put("USERNO", table.getUser());
                object.put("HINTS", "");
                object.put("TAXAMT", String.format(Locale.ENGLISH, "%.3f", itemTotal * item.getTax() / 100f));
                object.put("TAXABLE", item.getTax());
                object.put("VHFS", "0");
                object.put("VHFI", table.getOrderID());
                object.put("CREDITCARDNO", "");
                object.put("TRANSTYPE", "0");
                object.put("TRANSTYPESTR", "0.0000000000");
                object.put("ITEMOCODE", item.getId());
                object.put("ITEMONAMEA", toISO8859(language.equals("ar") ? item.getName() : item.getName2()));
                object.put("ITEMG", toISO8859(item.getCategory()));
                object.put("ORNo", "");
                object.put("OPOSNo", "2");
                object.put("OSerialNo", "-1");
                object.put("SalesManNo", "");
                object.put("StrNo", getRegisters().get("POS_STORE_NO"));
                object.put("AVGCOSTPRICE", "0");
                object.put("WPRICE", "1");
                object.put("SAVED", "1");
                object.put("PRINT", "1");
                object.put("ISFORCEQUESTION", item.isForceQ() ? "1" : "0");
                object.put("SERVICE", getRegisters().get("SERVICE_VALUE"));
                object.put("SERVICETAX", getRegisters().get("SERVICE_TAX"));
                object.put("SECTIONNO", table.getSection());
                object.put("ITEMU", "");
                object.put("ISPRINTEDITEM", "1");
                String note= item.getNote();
                note = note.replace("\n", " ").replace("\r", " ");
                note = toISO8859(note);

                object.put("ITEMREMARK", note);///
                //object.put("ITEMREMARK", toISO8859(item.getNote()));///
                object.put("ISPROMOTION", "0");
                object.put("PROMOTYPE", "0");
                object.put("WITHOUTSERVICE", "0");
                object.put("EWYC", "0");
                object.put("ISHH", "0");
                object.put("LINEDISCNO", "0");
                object.put("SNO", "");
                object.put("SNAME", "");
                object.put("TBLNO", table.getId());
                object.put("SECNO", table.getSection() != null ? table.getSection() : "");
                object.put("POSNOFORTEMPSERIAL", "0");
                object.put("ISHAPPY", "0");
                object.put("POINTAMT", "0");
                object.put("POINTCNT", "0");
                object.put("ISTAXEXMPT", "0");
                object.put("SECNM", getSections().get(table.getSection()) != null ? toISO8859(getSections().get(table.getSection())) : "-1");
                object.put("ITEMNAMEE", toISO8859(language.equals("en") ? item.getName() : item.getName2()));

                if (item.isTimeItem())
                {
                    try{
                        object.put("ITEMSTARTTIME",  new SimpleDateFormat(mySdf).format(item.getOpenTime()));//
                        object.put("ITEMENDTIME",  new SimpleDateFormat(mySdf).format(item.getEndTime()));//

                        if (item.isTimeItem())
                            object.put("ITEMSOLDBYHOUR", 1);
                        else
                            object.put("ITEMSOLDBYHOUR", 0);

                        object.put("STOP_ITEM_TIME", item.getIsFinishedTime());
                    }catch (Exception e)
                    {
                        object.put("ITEMSTARTTIME", "");
                        object.put("ITEMENDTIME", "");
                        object.put("ITEMSOLDBYHOUR", 0);
                        object.put("STOP_ITEM_TIME", 0);
                        e.printStackTrace();
                    }

                }
                else {
                    object.put("ITEMSTARTTIME", "");
                    object.put("ITEMENDTIME", "");
                    object.put("ITEMSOLDBYHOUR", 0);
                    object.put("STOP_ITEM_TIME", 0);
                }

                object.put("ITEMADDTIME", simpleTimeFormat.format(item.getAddTime()));

                array.put(object);
            }

            String trans = array.toString();


            object = new JSONObject();
            object.put("TSCNO", table.getSection());
            object.put("TBLNO", table.getId());
            object.put("NOOFSEATS", table.getSeatCount());
            object.put("VHFNo", table.getOrderID());
            object.put("VHFI", table.getOrderID());
            object.put("VHFDATE", simpleDateFormat.format(table.getDate()));
            object.put("CUSTOMERNO", "0");
            object.put("MERGETOTBL", "-1");
            object.put("CHANGETOTBL", "-1");
            object.put("TIMESTR", simpleDateFormat.format(table.getDate()));
            object.put("TIMEFNSH", simpleDateFormat.format(new Date()));
            object.put("TIMLONG", simpleDateFormat.format(new Date()));
            object.put("MINUTES", "0");
            object.put("STIMESTR", simpleTimeFormat.format(new Date()));
            object.put("STIMEFNSH", simpleTimeFormat.format(new Date()));
            object.put("STIMLONG", simpleTimeFormat.format(new Date()));
            object.put("WAITERNO", table.getUser());
            object.put("WAITERNAME", toISO8859(currentUser[0].getName()));
            object.put("REFUNDRSN", "");
            object.put("NOTE", "NOT COMPLETE");
            object.put("REMARK", "");
            object.put("STARTUSERNO", table.getUser());
            object.put("LASTUPDATETIME", simpleTimeFormat.format(new Date()));
            object.put("TEMPVHFNO", table.getTempVHFNO());

            String tableTransFinal = "[" + object.toString() + "]";

            object = new JSONObject();
            object.put("ISOPEN", "0");
            object.put("TBLSTATUS", "1");
            object.put("TSCNO", table.getSection());
            object.put("TBLNO", table.getId());

            String tables = "[" + object.toString() + "]";

            object = new JSONObject();
            object.put("TRANSNO", table.getTransNo());
            object.put("TRANSKIND", table.getTransKind());
            object.put("VHFNO", table.getOrderID());
            object.put("VHFDATE", simpleDateFormat.format(table.getDate()));
            object.put("POSNO", posNo);
            object.put("Disc", "0");
            object.put("CashP", table.getCash() - table.getChange());
            object.put("CouponP", "0");
            object.put("ChequeP", "0");
            object.put("CreditP", "0");
            object.put("CCName_F", "Visa");
            object.put("CCNo_F", "");
            object.put("CCAmt_F", table.getVisa());
            object.put("CCName_S", "Master");
            object.put("CCNo_S", "");
            object.put("CCAmt_S", table.getMaster());

            if (7 == 7)
                object.put("APaid", table.getTotal() + table.getTax());
            else
                object.put("APaid", table.getPayed());

            if (7 == 7)
                object.put("NetT", table.getTotal() + table.getTax() );
            else
                object.put("NetT", table.getTotal());

            object.put("CCName_T", "Other");
            object.put("CCNo_T", "");
            object.put("CCAmt_T", table.getOther());
            object.put("CLOSED", "");
            object.put("PAIDCARD", "");
            object.put("EXPORTED", "0");
            object.put("PREPAIDCARDNO", "");
            object.put("ISPAIDUPDATED", "");
            object.put("UPDATEDATE", simpleDateFormat.format(new Date()));
            object.put("UPDATEUSERNM", currentUser[0].getName());
            object.put("POINTSAMT", "0");
            object.put("POINTSCNT", "0");

            String paymethod = "[" + object.toString() + "]";

            String query;
            if (!isTakeOut) {

                if ((dateByPosNo.equals("1")) && ( ! posNo.equals("0")))
                {
                    query = "DB=1963-1972-1973-1976-&data=1&HEADER=%s&TRANS=%s&TABLETRANS=%s&TABLES=%s&UPDATE=%s&POS_NO="+posNo;
                    query = String.format(Locale.ENGLISH, query, header, trans, tableTransFinal, tables, update ? "1" : "0");
                }
                else
                {
                    query = "DB=1963-1972-1973-1976-&data=1&HEADER=%s&TRANS=%s&TABLETRANS=%s&TABLES=%s&UPDATE=%s";
                    query = String.format(Locale.ENGLISH, query, header, trans, tableTransFinal, tables, update ? "1" : "0");
                }
            } else {
                query = "DB=1963-1972-1973-1976-&data=1&HEADER=%s&TRANS=%s&TABLETRANS=%s&PAYMETHOD=%s&TakeAway=%s";
                query = String.format(Locale.ENGLISH, query, header, trans, tableTransFinal, paymethod, "1");
            }

            //execut the saving query
            String result = ConnectionManager.Query(query);

            if (result.contains("SAVING_ERROR"))
            {
                return false;
            }// DR7

            /*if (!(result.contains("before") && result.contains("after") && result.contains("->"))
                    || result.contains("SAVING_ERROR"))
            {
                return false;
            }// DR7*/


            if (!isTakeOut) {// by Darras to insure temp data saving only for the dine in

                String hQuery = "select count(*) as HCOUNT from \"Header_POSM_TEMP\" where VHFNO = '%s' and SECNO = '%s' and  TBLNO = '%s'";
                hQuery = String.format(hQuery, table.getOrderID(), table.getSection(), table.getId());
                query = "ID=1&type=59&DB=1963-1972-1973-1976-&data=1&QUERY=" + hQuery;
                result = ConnectionManager.getPHPData(query);
                try {
                    JSONArray array1 = new JSONArray(result);
                    for (int x = 0; x < array1.length(); x++) {
                        JSONObject object1 = array1.getJSONObject(x);
                        String hCount = object1.getString("HCOUNT");
                        if (hCount == null || hCount.equals("0")) {
                            table.setOrderID("");
                            return saveTable(table, context);
                        }
                    }
                } catch (Exception ex) {
                    return false;
                }

                hQuery = "select count(*) as HCOUNT from \"TransActn_POSM_TEMP\" where VHFNO = '%s' and SECNO = '%s' and  TBLNO = '%s'";
                hQuery = String.format(hQuery, table.getOrderID(), table.getSection(), table.getId());
                query = "ID=1&type=59&DB=1963-1972-1973-1976-&data=1&QUERY=" + hQuery;
                result = ConnectionManager.getPHPData(query);
                try {
                    JSONArray array1 = new JSONArray(result);
                    for (int x = 0; x < array1.length(); x++) {
                        JSONObject object1 = array1.getJSONObject(x);
                        String hCount = object1.getString("HCOUNT");
                        if (hCount == null || hCount.equals("0")) {
                            table.setOrderID("");
                            return saveTable(table, context);
                        }
                    }
                } catch (Exception ex) {
                    return false;
                }

            }

            object = new JSONObject();
            object.put("VHFNO", table.getOrderID());
            object.put("SECTION", toISO8859(sections.get(table.getSection())));
            object.put("VOUCHER_TOT", String.format(Locale.ENGLISH, "%.3f", table.getTotal() + table.getTax()));
            object.put("SECTION_NO", table.getSection());
            object.put("TBLNO", table.getId());
            object.put("ORDNO", table.getOrderID());
            object.put("CAPNAME", toISO8859(currentUser[0].getName()));
            object.put("CUSTORD", "");
            object.put("SEATNO", table.getSeatCount());
            object.put("REMARK", table.getNote());
            object.put("DISCNAME", "");
            object.put("KITTITLE", "");
            object.put("VDREASON", "");
            object.put("USERNO", currentUser[0].getId());
            object.put("CUSTOMER_NAME", "");
            object.put("TOTAL", String.format(Locale.ENGLISH, "%.3f", table.getTotal()));
            object.put("TAX", String.format(Locale.ENGLISH, "%.3f", table.getTax()));
            object.put("SERVICE", "0");
            object.put("SERVTAX", "0");


            String kdata = "[" + object.toString() + "]";

            HashMap<String, String> modifiersMap = new HashMap<>();
            modifiersMap.put("EXT", "1");
            modifiersMap.put("LT", "2");
            modifiersMap.put("NO", "4");
            modifiersMap.put("HF", "5");

            List<Item> newItemList = new ArrayList<>();
            array = new JSONArray();
            for (Item item : table.getItems()) {
                if (item.isOld()) continue;
                newItemList.add(item);
                for (Modifier modifier : item.getModifiers()) {
                    Item item1 = new Item();
                    item1.setId("");
                    item1.setName(String.format(" < %s > %s", modifier.getType(), modifier.getDesc()));
                    item1.setName2(item1.getName());
                    item1.setKind(modifiersMap.get(modifier.getType()));
                    newItemList.add(item1);
                    item.setParent(true);
                }

                for (Answer answer : item.getAnswers()) {
                    if (answer.getItem() != null) {
                        answer.getItem().setQty(item.getQty());
                        if (answer.getItem().useNewPrice()) {
                            answer.getItem().setPrice(answer.getItem().getNewPrice());
                        }
                        answer.getItem().setForceQ(true);
                        answer.getItem().setOld(item.isOld());
                        newItemList.add(answer.getItem());
                        item.setParent(true);
                    } else {
                        Item item1 = new Item();
                        item1.setId("");
                        item1.setName(answer.getDesc());
                        item1.setName2(item1.getName());
                        item1.setKind("3");
                        newItemList.add(item1);
                        item.setParent(true);
                    }
                }
            }
            for (Item item : newItemList) {
                float itemTotal = item.getQty() * item.getPrice();
                if (itemTotal == 0 && item.isParent()) {
                    continue;
                }
                String newName = toISO8859(item.getName());
                String newName2 = toISO8859(item.getName2());

                object = new JSONObject();
                object.put("VHFNO", table.getOrderID());
                object.put("ITEM_CODE", item.getId());
                object.put("ITEM_NAMEA", newName);
                object.put("ITEM_NAME", newName2);
                object.put("QTY", item.getQty());
                object.put("ITEM_REMARK", toISO8859(item.getNote()));
                object.put("PRICE", String.format(Locale.ENGLISH, "%.3f", item.getPrice()));
                object.put("PRICE_TOT", String.format(Locale.ENGLISH, "%.3f", itemTotal));
                object.put("NOTE", toISO8859(item.getNote()));
                object.put("IS_SUB_ITEM", item.isForceQ() ? "1" : "0");
                object.put("KIND", item.getKind());
                object.put("OLD_QTY", item.getOldQty());
                object.put("REASON", "");
                object.put("TSCNO", table.getSection());
                object.put("TBLNO", table.getId());

                array.put(object);
            }

            String kitems = array.toString();

            array = new JSONArray();
            for (Item item : table.getRefundItems()) {
                if (!item.isOld()) continue;
                object = new JSONObject();
                object.put("VHFNO", table.getOrderID());
                object.put("ITEM_CODE", item.getId());
                object.put("ITEM_NAME", toISO8859(item.getName()));
                object.put("PRICE", item.getPrice());
                object.put("QTY", item.getQty());
                object.put("TAXPERC", item.getTax());
                object.put("ITEM_GROUP", toISO8859(item.getCategory()));
                object.put("REASON", item.getRefundReason().getDesc());
                object.put("PRINTORNOT", "0");
                object.put("KIND", "0");
                object.put("TSCNO", table.getSection());
                object.put("TBLNO", table.getId());

                array.put(object);
            }
            String kvoidItems = array.toString();

            query = "DB=1963-1972-1973-1976-&data=1&ID=5&type=6&KDATA=%s&KITEMS=%s&KVOIDITEMS=%s";
            query = String.format(Locale.ENGLISH, query, kdata, kitems, kvoidItems);

            result = ConnectionManager.Query(query);
            System.out.println("Kitchen " + result);


            if (table.getRefundItems().size() > 0) {
                array = new JSONArray();
                for (Item item : table.getRefundItems()) {
                    object = new JSONObject();
                    object.put("VHFNO", table.getOrderID());
                    object.put("POSNO", posNo);
                    object.put("TSCNO", table.getSection());
                    object.put("TBLNO", table.getId());
                    object.put("IOQTY", item.getQty());
                    object.put("PRICE", item.getPrice());
                    object.put("TOTAL", item.getPrice() * item.getQty());
                    object.put("CUSTOMERNO", "");
                    object.put("CUSTOMERNAME", "");
                    object.put("VHFDATE", simpleDateFormat.format(table.getDate()));
                    object.put("VHFTIME", simpleTimeFormat.format(table.getDate()));
                    object.put("SERVERNO", "1000");
                    object.put("REASON", item.getRefundReason().getDesc());
                    object.put("TAXABLE", item.getTax());
                    object.put("VHFI", table.getOrderID());
                    object.put("ITEMOCODE", item.getId());
                    object.put("ITEMONAMEA", toISO8859(item.getName()));
                    object.put("ITEMG", toISO8859(item.getCategory()));
                    object.put("PRINTORNOT", "0");
                    object.put("NOTE", item.isOld() ? "COMPLETE" : "NOT COMPLETE");
                    object.put("ISFORCEQUESTION", item.isForceQ() ? "1" : "0");
                    object.put("VOIDORCANCEL", item.isRefundOrCancel() ? "1" : "0");

                    array.put(object);
                }

                String voidItems = array.toString();

                query = "DB=1963-1972-1973-1976-&data=1&ID=5&type=5&VOIDREASON=%s";
                query = String.format(Locale.ENGLISH, query, voidItems);

                result = ConnectionManager.Query(query);
                System.out.println("Void " + result);
            }



            DataManager.sendUDP(table,context);//sendUDPTablesUpdate(context, table);



            if (isTakeOut)
            {
                query = "DB=1963-1972-1973-1976-&POSDAILYCM=1";
                result = ConnectionManager.Query(query);
            }

            return true;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void refreshTables() {
        String query = "ID=1&type=9&DB=1963-1972-1973-1976-&data=1";
        String data = ConnectionManager.getPHPData(query);
        try {
            JSONObject obj = new JSONObject(data);
            System.out.println("x");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static int getDeviceType(Context context) {
        SharedPreferences sp = Constants.getSharedPrefs(context);
        return sp.getInt(Constants.deviceType, DEVICE_TYPE_DINE_IN);
    }

    public static boolean isLoading = false;

    public static void readTablesOnly(Context context) {
        String query;
        SharedPreferences sp = Constants.getSharedPrefs(context);
        posNo =  sp.getString(Constants.posNO, "");

        dateByPosNo = sp.getString(Constants.dateByPosNo, "0");;

        if ((dateByPosNo.equals("1")) && (posNo != null))
            query = "ID=3&type=1&DB=1963-1972-1973-1976-&data=1&POS_NO="+posNo;
        else
            query = "ID=3&type=1&DB=1963-1972-1973-1976-&data=1";

        String data = ConnectionManager.getPHPData(query);
        JSONObject obj = null;
        try {
            obj = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        try {
//            //reading groups
//            JSONArray groups = obj.getJSONArray("GROUPS");
//            categories.clear();
//            for (int x = 0; x < groups.length(); x++) {
//                String group = groups.getJSONObject(x).getString("Desc_Name");
//                Category category = new Category();
//                category.setId(group);
//                categories.put(group, category);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //reading tables
        try {
            JSONArray TablesArray = obj.getJSONArray("TABLES");
            tables.clear();
            for (int x = 0; x < TablesArray.length(); x++) {
                JSONObject object = TablesArray.getJSONObject(x);
                String section = object.getString("SECNO");
                String id = object.getString("TBLNO");
                String status = object.getString("TBLSTATUS");
                String x_axis = object.getString("X_AXIS");
                String y_axis = object.getString("Y_AXIS");
                String tableUser = object.getString("TABLEUSER");

                Table table = new Table();
                table.setSection(section);
                table.setId(id);
                table.setDesc(id);
                table.setStatus(status);
                table.setColor(getColor(context, status));
                table.setX(Float.valueOf(x_axis));
                table.setY(Float.valueOf(y_axis));
                table.setUser(tableUser);
                tables.add(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readAllPHP(Context context) {


        while (isLoading) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isLoading = true;

        try {

            User defUser = new User();
            defUser.setId("f123f");
            defUser.setName("falcons");
            defUser.setMaster("1");
            users.put(defUser.getId(), defUser);

            SharedPreferences sp = Constants.getSharedPrefs(context);
            String language = sp.getString(Constants.language, "en");


            String storeNO = sp.getString(Constants.storeNO, "");
            String query;
            if (!storeNO.trim().equals(""))
            {
                query = "ID=3&type=1&DB=1963-1972-1973-1976-&data=1&STORE="+storeNO;
            }else
            {
                query = "ID=3&type=1&DB=1963-1972-1973-1976-&data=1";
            }

            String data = ConnectionManager.getPHPData(query);
            String POSSP = sp.getString(Constants.posNO, "");
            String sectionsByPOS = sp.getString(Constants.sectionsByPos, "");

            JSONObject obj = new JSONObject(data);

            Log.e("*************obj",""+obj.toString());
            try {
                //reading groups
                JSONArray groups = obj.getJSONArray("GROUPS");
                categories.clear();
                for (int x = 0; x < groups.length(); x++) {
                    String group = groups.getJSONObject(x).getString("Desc_Name");
//                    String group_url_image = groups.getJSONObject(x).getString("Desc_Name");
                    Category category = new Category();
                    category.setId(group);
                    category.setUrlImage("https://picsum.photos/id/1/200/300");
                    categories.put(group, category);
                    Log.e("obj","GROUPS"+group.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                //reading items
                JSONArray ItemsArray = obj.getJSONArray("ITEMS");
                Log.e("setItemDescription","ItemsArray"+ItemsArray.length());
                Log.e("setItemDescription","ItemsArray"+ItemsArray.toString());

                items.clear();
                for (int x = 0; x < ItemsArray.length(); x++) {
                    JSONObject object = ItemsArray.getJSONObject(x);
                    String id = object.getString("SITEMCODE");
                    Log.e("setItemDescription","SITEMCODE"+id.toString());
                    if(id.equals("150")){
                        Log.e("setItemDescription",id+"====="+object.getString("ITEM_DESCRIPTION")+"\t"+object.getString("ITEMPICSPATH"));
                    }
                    String price = object.getString("SITEMPRICE");
                    String namea = language.equals("ar") ? object.getString("SITEMNAME") : object.getString("SITEMNAMEE");
                    String name = language.equals("ar") ? object.getString("SITEMNAMEE") : object.getString("SITEMNAME");
                    String mCategory = object.getString("SITEMGROUP");
                    String tax = object.getString("NTAXPERC");
                    String show = object.getString("SSHOWMENU");
                    String picPath="https://picsum.photos/id/2/200/300";
                  try {
                       picPath=object.getString("ITEMPICSPATH");
                      Log.e("setItemDescription",""+object.getString("ITEM_DESCRIPTION"));
                  }catch (Exception e){

                  }

//                    String itemDesc= object.getString("SSHOWMENU");

                    if (show.equals("0")) continue;

                    Item item = new Item();
                    item.setId(id);
                    item.setName(namea);
                    Log.e("setItemDescription","setName"+namea);
                    item.setName2(name);
                    item.setPrice(Float.valueOf(price));
                    item.setTax(Float.valueOf(tax));
                    item.setCategory(mCategory);
                    item.setSubItem(false);

//
                    if(picPath.equals("")){
                        item.setimagePathItem("https://picsum.photos/id/2/200/300");
                    }else  item.setimagePathItem(picPath);
                    try {
                        item.setItemDescription(object.getString("ITEM_DESCRIPTION"));
                    }catch (Exception e){
                        item.setItemDescription("description");
                    }

                    Log.e("setItemDescription",""+item.getItemDescription());
                    try
                    {
                        if (object.getInt("ITEMSOLDBYHOUR") == 1)
                            item.setTimeItem(true);
                        else
                            item.setTimeItem(false);
                    }catch (Exception e)
                    {
                        item.setTimeItem(false);
                    }

                /*
                if (question.length() > 0) {
                    if (questions.containsKey(question)) {
                        Question q = questions.get(question);
                        item.getQuestions().add(q);
                    }
                }*/
                    DataManager.items.put(id, item);
                    Log.e("setItemDescription","DataManager.items="+DataManager.items.size());
                }
            } catch (Exception e) {
                Log.e("setItemDescription",""+e.getMessage());
                e.printStackTrace();
            }


            //reading modifiers
            try {
                JSONArray ModifiersArray = obj.getJSONArray("MODIFIERS");
                for (int x = 0; x < ModifiersArray.length(); x++) {
                    JSONObject object = ModifiersArray.getJSONObject(x);
                    String id = object.getString("SMODIFIER_ID");
                    String category = object.getString("SGOUPDESC");
                    String desc = object.getString("SMODIFIER_DESC");

                    Modifier modifier = new Modifier();
                    modifier.setId(id);
                    modifier.setDesc(desc);
                    if (categories.containsKey(category)) {
                        categories.get(category).getModifiers().add(modifier);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //reading tables
            try {
                JSONArray TablesArray = obj.getJSONArray("TABLES");
                tables.clear();
                for (int x = 0; x < TablesArray.length(); x++) {
                    JSONObject object = TablesArray.getJSONObject(x);
                    String section = object.getString("SECNO");
                    String id = object.getString("TBLNO");
                    String status = object.getString("TBLSTATUS");
                    String x_axis = object.getString("X_AXIS");
                    String y_axis = object.getString("Y_AXIS");
                    String tableUser = object.getString("TABLEUSER");

                    Table table = new Table();
                    table.setSection(section);
                    table.setId(id);
                    table.setDesc(id);
                    table.setStatus(status);
                    table.setColor(getColor(context, status));
                    table.setX(Float.valueOf(x_axis));
                    table.setY(Float.valueOf(y_axis));
                    table.setUser(tableUser);
                    tables.add(table);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            //forced questions
            try {
                JSONArray ForcedQArray = obj.getJSONArray("FORCEDQUESTIONS");
                questions.clear();
                for (int x = 0; x < ForcedQArray.length(); x++) {
                    JSONObject object = ForcedQArray.getJSONObject(x);
                    String id = object.getString("SQNO");
                    String type = object.getString("NMULTIPLE");
                    Question question = new Question();
                    question.setType(type);
                    question.setId(id);
                    questions.put(question.getId(), question);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //forced questions items
            try {
                JSONArray ForcedQItemsArray = obj.getJSONArray("FORCEQITEMS");
                for (int x = 0; x < ForcedQItemsArray.length(); x++) {
                    JSONObject object = ForcedQItemsArray.getJSONObject(x);
                    String itemCode = object.getString("SFQ_ITEMCODE");
                    String qNo = object.getString("SQNO");
                    String desc = object.getString("SFQ_DESC");

                    Question question = questions.get(qNo);
                    if (question == null) continue;
                    question.setDesc(desc);

                    Item item = items.get(itemCode);
                    if (item == null) continue;
                    item.getQuestions().add(question);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                //forced answer
                JSONArray ForcedQAnswersArray = obj.getJSONArray("FORCEDANSWERS");
                for (int x = 0; x < ForcedQAnswersArray.length(); x++) {
                    JSONObject object = ForcedQAnswersArray.getJSONObject(x);
                    String id = object.getString("SMODNO");
                    String qNo = object.getString("SQNO");
                    String desc = object.getString("SANSWERNAME");

                    Answer answer = new Answer();
                    answer.setqId(id);
                    answer.setDesc(desc);
                    answer.setmId(id);
                    Question question = questions.get(qNo);
                    if (question == null) continue;
                    question.getAnswers().add(answer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                //refund reasons
                JSONArray RefundReasonsArray = obj.getJSONArray("REFUNDREASON");
                refundReasons.clear();
                for (int x = 0; x < RefundReasonsArray.length(); x++) {
                    JSONObject object = RefundReasonsArray.getJSONObject(x);
                    String id = object.getString("SERIALNO");
                    String desc = object.getString("REFUNDDESC");
                    String reduceInv = object.getString("REDUCEINV");
                    String printOnReport = object.getString("PRINTONREPT");

                    RefundReason refundReason = new RefundReason();
                    refundReason.setId(id);
                    refundReason.setDesc(desc);
                    refundReason.setReduceInv(reduceInv);
                    refundReason.setPrintOnReport(printOnReport);

                    refundReasons.add(refundReason);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //refund reasons
            try {
                JSONArray forceQuestionsItems = obj.getJSONArray("FORCEQUESTIONSITEMS");
                for (int x = 0; x < forceQuestionsItems.length(); x++) {
                    JSONObject object = forceQuestionsItems.getJSONObject(x);
                    String qId = "F*" + object.getString("QUESTIONNO");
                    String itemID = object.getString("ITEMOCODE");
                    // String itemName = object.getString("ITEMNAME");
                    // String qCount = object.getString("NUMOFQUESTION");
                    String qText = object.getString("QUESTIONTEXT");
                    String aCount = object.getString("ANSWERNUM");
                    // String defaultAnswers = object.getString("DEFAULTANSWERS");
                    // String isTemplate = object.getString("ISTEMPLATE");
                    Question question = new Question();
                    question.setId(qId);
                    question.setDesc(qText);
                    question.setType("2");
                    question.setAnswersCount(Integer.valueOf(aCount));
                    questions.put(qId, question);

                    Item item = items.get(itemID);
                    if (item == null) continue;
                    item.getQuestions().add(question);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                JSONArray subItemsArray = obj.getJSONArray("SUBITEMS");
                for (int x = 0; x < subItemsArray.length(); x++) {
                    JSONObject object = subItemsArray.getJSONObject(x);
                    String qId = "F*" + object.getString("QUESTIONNO");
                    String itemID = object.getString("FQITEMOCODE");
                    String itemName = object.getString("FQITEMNAME");
                    String itemName2 = object.getString("FQITEMNAMEA");
                    // String qCount = object.getString("NUMOFQUESTION");
                    String doSave = object.getString("FQSAVED");
                    String doPrint = object.getString("FQPRINT");
                    String caption = object.getString("POSCAPTION");
                    String useNewPrice = object.getString("USENEWPRICE");
                    String category = object.getString("FQITEMGROUP");
                    // String defaultAnswers = object.getString(DEFAULTANSWERS");
                    // String isTemplate = object.getString("ISTEMPLATE");
                    String taxPer = object.getString("TAXPERC");
                    String price = object.getString("FQITEMPRICE");
                    String newPrice = object.getString("FQNEWPRICE");
                    String fqwithprice = object.getString("FQWITHPRICE");
//                    String description =  object.getString("ITEM_DESCRIPTION");
//                    Log.e("setItemDescription","=="+description);
                    if (!items.containsKey(itemID)) {
                        Item item = new Item();
                        item.setId(itemID);
                        item.setDummyDesc(itemName);
                        item.setimagePathItem("https://picsum.photos/id/1/200/300");
//                        item.setItemDescription(description);
//                        Log.e("setItemDescription",""+item.getItemDescription());
                        item.setName2(itemName2);
                        item.setName(itemName);
                        item.setPrice(Float.valueOf(price));
                        item.setTax(Float.valueOf(taxPer));
                        item.setCategory(category);//category dr7
                        item.setSubItem(true);
                        items.put(itemID, item);
                    }
                    SubItem subItem = getSubItemCopy(items.get(itemID));
                    if (subItem == null) continue;
                    if (caption.equals("null")) {
                        caption = subItem.getName();
                    }

                    subItem.setDoSave(doSave.equals("1"));
                    subItem.setDoPrint(doPrint.equals("1"));
                    subItem.setCaption(caption);
                    subItem.setUseNewPrice(useNewPrice.equals("1"));
                    subItem.setNewPrice(Float.valueOf(newPrice));
                    if (fqwithprice.equals("0")) {
                        subItem.setUseNewPrice(false);
                        subItem.setPrice(0f);
                        subItem.setNewPrice(0f);
                    }

                    if (subItem.useNewPrice())
                         subItem.setPrice(Float.valueOf(newPrice));


                    Question question = questions.get(qId);
                    if (question == null) continue;

                    Answer answer = new Answer();
                    answer.setqId(qId);
                    answer.setSubItem(subItem);
                    answer.setDesc(caption);
                    question.getAnswers().add(answer);
                    if (itemID.equals("141")) {
                        System.out.println("aa");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                JSONArray sectionsArray = obj.getJSONArray("TABLESECTIONS");
                sections.clear();
                for (int x = 0; x < sectionsArray.length(); x++) {
                    JSONObject object = sectionsArray.getJSONObject(x);
                    Log.e("obj","TABLESECTIONS"+object.toString());
                    String id = object.getString("SECNO");
                    String eName = object.getString("SECNAME");
                    String aName = object.getString("SECNAMEA");
                    String POSNO = object.getString("POS_NO");
                    String name = language.equals("ar") ? aName : eName;
                    Log.e("obj","TABLESECTIONS=eName="+eName.toString());
                    if (sectionsByPOS.equals("1"))
                    {
                        if (POSNO.equals(POSSP))
                            sections.put(id, name);
                    }
                    else
                    {
                        sections.put(id, name);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                JSONArray usersArray = obj.getJSONArray("USERS");

                users.clear();
                User user = new User();
                user.setId("f123f");
                user.setName("falcons");
                user.setMaster("1");
                users.put(user.getId(), user);

                for (int x = 0; x < usersArray.length(); x++) {
                    JSONObject object = usersArray.getJSONObject(x);
                    String id = object.getString("USERNO");
                    String name = object.getString("USERNAME");
                    String master = object.getString("Master");

                    user = new User();
                    user.setId(id);
                    user.setName(name);
                    user.setMaster(master);

                    users.put(id, user);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                JSONArray registersArray = obj.getJSONArray("REGISTERS");
                registers.clear();
                for (int x = 0; x < registersArray.length(); x++) {
                    JSONObject object = registersArray.getJSONObject(x);
                    String id = object.getString("REGISTER_NAME");
                    String name = object.getString("REGISTER_VALUE");

                    registers.put(id, name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            System.out.println(obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        isLoading = false;
    }

    public static List<RefundReason> getRefundReasons() {
        return refundReasons;
    }

    public static boolean logout(String user) {
        return true;
    }

    public static LinkedHashMap<String, Question> getQuestions() {
        if (questions.size() == 0) {
            try {
                String query = "select id,description,aCount from questionHeader";
                String data = ConnectionManager.Query(query);
                String[] lines = data.split("\n", -1);
                for (String line : lines) {
                    String[] fields = line.split(";", -1);
                    if (fields.length > 2) {
                        Question question = new Question();
                        question.setId(fields[0]);
                        question.setDesc(fields[1]);
                        question.setAnswersCount(Integer.valueOf(fields[2]));

                        questions.put(question.getId(), question);
                    }
                }

                query = "select id,description from questionDetails";
                data = ConnectionManager.Query(query);
                lines = data.split("\n", -1);
                for (String line : lines) {
                    String[] fields = line.split(";", -1);
                    if (fields.length > 1) {
                        if (!questions.containsKey(fields[0])) {
                            continue;
                        }
                        Answer answer = new Answer();
                        answer.setqId(fields[0]);
                        answer.setDesc(fields[1]);

                        questions.get(answer.getqId()).getAnswers().add(answer);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return questions;
    }

    public static SubItem getSubItemCopy(Item item) {
        if (item == null) return null;
        SubItem newItem = new SubItem();
        newItem.setId(item.getId());
        newItem.setName(item.getName());
        newItem.setName2(item.getName2());
        newItem.setCategory(item.getCategory());//
        newItem.setTax(item.getTax());
        newItem.setPrice(item.getPrice());
        newItem.setDummyColor(item.getDummyColor());
        newItem.setDummyDesc(item.getDummyDesc());
        item.setimagePathItem("https://picsum.photos/id/1/200/300");
        item.setItemDescription(item.getItemDescription());
        newItem.setTimeItem(item.isTimeItem());

        List<Modifier> newModifiers = new ArrayList<>();
        List<Question> newQuestions = new ArrayList<>();
        for (Modifier modifier : item.getOrgModifiers()) {
            Modifier newModifier = new Modifier();
            newModifier.setId(modifier.getId());
            newModifier.setDesc(modifier.getDesc());
            newModifier.setType(modifier.getType());
            newModifiers.add(newModifier);
        }
        for (Question question : item.getQuestions()) {
            Question newQuestion = new Question();
            newQuestion.setAnswers(new ArrayList<>(question.getAnswers()));
            newQuestion.setId(question.getId());
            newQuestion.setType(question.getType());
            newQuestion.setDesc(question.getDesc());
            newQuestion.setAnswersCount(question.getAnswersCount());
            newQuestions.add(newQuestion);
        }
        newItem.setOrgModifiers(newModifiers);
        newItem.setQuestions(newQuestions);
        return newItem;
    }

    public static Item getItemCopy(Item item) {

        if (item == null) return null;

        System.out.println("ITEM NOOOOTES!!" + item.getNotes());

        Item newItem = new Item();
        newItem.setId(item.getId());
        newItem.setName(item.getName());
        newItem.setName2(item.getName2());
        newItem.setCategory(item.getCategory());
        newItem.setTax(item.getTax());
        newItem.setPrice(item.getPrice());
        newItem.setNotes(item.getNotes());
        newItem.setDummyColor(item.getDummyColor());
        newItem.setDummyDesc(item.getDummyDesc());
        item.setimagePathItem("https://picsum.photos/id/1/200/300");
        newItem.setTimeItem(item.isTimeItem());
        item.setItemDescription(item.getItemDescription());

        List<Modifier> newModifiers = new ArrayList<>();
        List<Question> newQuestions = new ArrayList<>();
        for (Modifier modifier : item.getOrgModifiers()) {
            Modifier newModifier = new Modifier();
            newModifier.setId(modifier.getId());
            newModifier.setDesc(modifier.getDesc());
            newModifier.setType(modifier.getType());
            newModifiers.add(newModifier);
        }
        int x = 0;
        for (Question question : item.getQuestions()) {
            Question newQuestion = new Question();
            newQuestion.setAnswers(new ArrayList<Answer>());
            newQuestion.setId(question.getId() + "_" + x);
            for (Answer answer : question.getAnswers()) {
                Answer nAnswer = new Answer();
                nAnswer.setqId(newQuestion.getId());
                nAnswer.setmId(answer.getmId());
                nAnswer.setDesc(answer.getDesc());
                nAnswer.setSubItem(answer.getItem());
                newQuestion.getAnswers().add(nAnswer);
            }
            newQuestion.setType(question.getType());
            newQuestion.setDesc(question.getDesc());
            newQuestion.setAnswersCount(question.getAnswersCount());
            newQuestions.add(newQuestion);
            x++;
        }
        newItem.setOrgModifiers(newModifiers);
        newItem.setQuestions(newQuestions);
        return newItem;
    }

    private static int getColor(Context context, String status) {
        SharedPreferences sp = Constants.getSharedPrefs(context);
        switch (status) {
            case "0":
                String color = sp.getString(Constants.availableColor, "blue");
                int drawableColor = R.drawable.card_background_blue;
                switch (color) {
                    case "blue":
                        drawableColor = R.drawable.card_background_blue;
                        break;
                    case "green":
                        drawableColor = R.drawable.card_background_green;
                        break;
                    case "yellow":
                        drawableColor = R.drawable.card_background_yellow;
                        break;
                    case "red":
                        drawableColor = R.drawable.card_background_red;
                        break;
                    case "grey":
                        drawableColor = R.drawable.card_background_grey_dark;
                        break;
                }
                return drawableColor;
            case "1":
                color = sp.getString(Constants.usedColor, "green");
                drawableColor = R.drawable.card_background_green;
                switch (color) {
                    case "blue":
                        drawableColor = R.drawable.card_background_blue;
                        break;
                    case "green":
                        drawableColor = R.drawable.card_background_green;
                        break;
                    case "yellow":
                        drawableColor = R.drawable.card_background_yellow;
                        break;
                    case "red":
                        drawableColor = R.drawable.card_background_red;
                        break;
                    case "grey":
                        drawableColor = R.drawable.card_background_grey_dark;
                        break;
                }
                return drawableColor;
            case "2":
                color = sp.getString(Constants.finishSoonColor, "yellow");
                drawableColor = R.drawable.card_background_yellow;
                switch (color) {
                    case "blue":
                        drawableColor = R.drawable.card_background_blue;
                        break;
                    case "green":
                        drawableColor = R.drawable.card_background_green;
                        break;
                    case "yellow":
                        drawableColor = R.drawable.card_background_yellow;
                        break;
                    case "red":
                        drawableColor = R.drawable.card_background_red;
                        break;
                    case "grey":
                        drawableColor = R.drawable.card_background_grey_dark;
                        break;
                }
                return drawableColor;
            case "3":
                color = sp.getString(Constants.reservedColor, "red");
                drawableColor = R.drawable.card_background_red;
                switch (color) {
                    case "blue":
                        drawableColor = R.drawable.card_background_blue;
                        break;
                    case "green":
                        drawableColor = R.drawable.card_background_green;
                        break;
                    case "yellow":
                        drawableColor = R.drawable.card_background_yellow;
                        break;
                    case "red":
                        drawableColor = R.drawable.card_background_red;
                        break;
                    case "grey":
                        drawableColor = R.drawable.card_background_grey_dark;
                        break;
                }
                return drawableColor;
            case "4":
                color = sp.getString(Constants.lockedColor, "grey");
                drawableColor = R.drawable.card_background_grey_dark;
                switch (color) {
                    case "blue":
                        drawableColor = R.drawable.card_background_blue;
                        break;
                    case "green":
                        drawableColor = R.drawable.card_background_green;
                        break;
                    case "yellow":
                        drawableColor = R.drawable.card_background_yellow;
                        break;
                    case "red":
                        drawableColor = R.drawable.card_background_red;
                        break;
                    case "grey":
                        drawableColor = R.drawable.card_background_grey_dark;
                        break;
                }
                return drawableColor;

            default:
                return R.drawable.card_background_blue;
        }
    }

    public static LinkedHashMap<String, Category> getCategories() {
        if (categories.size() == 0) {
            String query = "Select id from Categories order by cIndex";
            String data = ConnectionManager.Query(query);
            String[] lines = data.split("\n", -1);
            for (String line : lines) {
                String[] fields = line.split(";", -1);
                if (fields.length > 0) {
                    String id = fields[0];
                    Category category = new Category();
                    category.setId(id);
                    category.setUrlImage("https://picsum.photos/id/1/200/300");
                    Log.e("categories","1"+category.getUrlImage());
                    categories.put(id, category);
                }
            }

            query = "Select id,section from modifiers";
            data = ConnectionManager.Query(query);
            lines = data.split("\n", -1);
            for (String line : lines) {
                String[] fields = line.split(";", -1);
                if (fields.length > 1) {
                    String id = fields[0];
                    Category category = categories.get(id);
                    Log.e("categories","2"+category.getUrlImage());
                    if (category == null) continue;

                    Modifier modifier = new Modifier();
                    modifier.setId(fields[1]);
                    category.getModifiers().add(modifier);
                }
            }


        }
        return categories;
    }

    public static boolean checkLogin(String user, Context context) {
        if (users.containsKey(user)) {
            currentUser[0] = users.get(user);
            SharedPreferences sp = Constants.getSharedPrefs(context);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(Constants.userID, user);
            editor.apply();
            return true;
        }
        return false;
    }

    public static List<Table> getTables(String section) {
        List<Table> tables = new ArrayList<>();
        if (DataManager.tables.size() > 0) {
            if (section == null) {
                return new ArrayList<>(DataManager.tables);
            }
            for (Table table : DataManager.tables) {
                if (table.getSection().equals(section)) {
                    tables.add(table);
                }
            }
            return tables;
        }

        return tables;
    }

    public static LinkedHashMap<String, String> getSections() {
        return sections;
    }
// fill items and sub items
    public static LinkedHashMap<String, Item> getItems(String category) {
        LinkedHashMap<String, Item> items = new LinkedHashMap<>();

        if (DataManager.items.size() > 0) {
            for (Item item : DataManager.items.values()) {
                if (category == null) {
                    items.put(item.getId(), item);
                    continue;
                }
                if (item.getCategory().equals(category) && (!item.isSubItem()))  {
                    items.put(item.getId(), item);
                }
            }
        }

        return items;
    }

    public static String DoQuery(String query) {
        try {
            System.out.println(query);
            query = URLEncoder.encode(query, "utf-8");
            query = String.format("ID=2&type=802&DB=1963-1972-1973-1976-&data=1&QUERY=%s", query);
            String result = ConnectionManager.Query(query);
            return result.length() == 0 ? "Successful" : result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    public static String getData(String query) {
        try {
            System.out.println(query);
            query = URLEncoder.encode(query, "utf-8");
            query = String.format("ID=1&type=59&DB=1963-1972-1973-1976-&data=1&QUERY=%s", query);
            return ConnectionManager.getPHPData(query);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }
}
