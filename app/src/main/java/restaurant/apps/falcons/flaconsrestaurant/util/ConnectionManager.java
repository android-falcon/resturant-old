package restaurant.apps.falcons.flaconsrestaurant.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by pure_ on 30/07/2016.
 */

public class ConnectionManager {
    public static String serverURL = "http://192.168.1.6:8085/resturant";

    public static String getPHPData(String query) {
        try {
            String link = serverURL + "/index_iPad.php";
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(60 * 1000);
            connection.setReadTimeout(60 * 1000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=windows-1256");
            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(query.length()));
            connection.setRequestProperty("accept-charset", "windows-1256");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(query);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                    "windows-1256"));
            StringBuffer br = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                if (br.length() > 0) {
                    br.append("\n");
                }
                br.append(line);
            }
            in.close();
            connection.disconnect();
            System.out.println(br.toString());
            return br.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    static String Query(String query) {
        try {
            //query = URLEncoder.encode(query, "utf-8");
            String link = serverURL + "/insert_iPad.php";
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(60 * 1000);
            connection.setReadTimeout(60 * 1000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=utf-8");
            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(query.length()));
            connection.setRequestProperty("accept-charset", "utf-8");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(query);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                    "utf-8"));
            StringBuffer br = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                if (br.length() > 0) {
                    br.append("\n");
                }
                br.append(line);
            }
            in.close();
            connection.disconnect();
            System.out.println(br.toString());
            return br.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "" + ex.getMessage();
        }
    }

    public static String DoQuery(String query) {
        try {
            //query = "query=" + URLEncoder.encode(query, "windows-1256");
            System.out.println(query);
            String link = serverURL + "/DoQueryServlet";
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(60 * 1000);
            connection.setReadTimeout(60 * 1000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=windows-1256");
            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(query.getBytes().length));
            connection.setRequestProperty("accept-charset", "windows-1256");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(query);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                    "windows-1256"));
            StringBuffer br = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                if (br.length() > 0) {
                    br.append("\n");
                }
                br.append(line);
            }
            in.close();
            connection.disconnect();
            System.out.println(br.toString());
            return br.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }
}
