package restaurant.apps.falcons.flaconsrestaurant.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class SendSocket {

    Context context;
    JSONObject obj;
//    DatabaseHandler db;

    public SendSocket(Context context) {
        this.context = context;
//        db = new DatabaseHandler(context);
    }

    public void sendMessage(String table,String seats,String curentDate) {
        Log.e("obj3Socket","sendMessage");
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

//                Socket s = null;
                OutputStream out = null;
                PrintWriter output = null;


                //this for_loop for filter and send all data to target kitchen has same kitchen no and have ip

//                    if (!kitchenScreens.get(i).getKitchenIP().equals("")) {//&& isHostAvailable(kitchenScreens.get(i).getKitchenIP(), 9002,100)
//                            if (checkHosts("10.10.100.22")){
//                            if (obj3.toString().length() > 2) {
                                try {
                                    Log.e("obj3Socket","sendMessage///in");
                                    String ip="192.168.2.7";
                                    Socket  s = new Socket("192.168.2.57", 9009);


                                    out = s.getOutputStream();
                                    output = new PrintWriter(out);
                                    output.println("table#"+" "+table+","+"SEC#"+" "+seats+","+"#DATE"+" "+curentDate);
                                    output.flush();
                                    Log.e("obj3Socket","send");
                                    output.close();
                                    out.close();
                                    s.close();
                                } catch (IOException e) {
                                    Log.e("obj3Socket","sendMessage"+e.getMessage());
                                    e.printStackTrace();
                                }
//                            }
//                        }//

//                    }


                //tis for read data send from server ...

//                    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
//                    final String st = input.readLine();

//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
////                            String s = mTextViewReplyFromServer.getText().toString();
////                            if (st.trim().length() != 0)
////                                mTextViewReplyFromServer.setText(s + "\nFrom Server : " + st);
////                            if (st.trim().length() != 0)
////                                Toast.makeText(context, "From Server : successful Socket " + st, Toast.LENGTH_SHORT).show();
//                        }
//                    });


            }
        });

        thread.start();
    }

    // this for get all object  send to target kitchen by kitchen no ...

//    JSONArray getObjectForKitchenNo(List<OrderTransactions> orderTransactions ,int KitchenNo) {
//
//        JSONArray objNo = new JSONArray();
//
//        for (int i = 0; i < orderTransactions.size(); i++) {
//            if (orderTransactions.get(i).getScreenNo() == KitchenNo) {
//                try {
//                    JSONObject obj = new JSONObject();
//                    Log.e("ISUPDATE  =1","1234 ==>"+orderTransactions.get(i).getOrderKind()+"    "+orderTransactions.get(i).toString());
//
//                    String Date =orderTransactions.get(i).getVoucherDate()+" "+orderTransactions.get(i).getTime();
//                    Log.e("date "," ==>"+Date);
//
//                    obj.put("TRINDATE", Date);
//                    obj.put("ITEMCODE", orderTransactions.get(i).getItemBarcode());
//                    obj.put("ITEMNAME", orderTransactions.get(i).getItemName());
//                    obj.put("QTY", orderTransactions.get(i).getQty());
//                    obj.put("PRICE", orderTransactions.get(i).getPrice());
//                    obj.put("NOTE", orderTransactions.get(i).getNote());
////                    obj.put("SCREENNO", orderTransactions.get(i).getScreenNo());
//                    obj.put("POSNO", orderTransactions.get(i).getPosNo());
//                    obj.put("ORDERNO", orderTransactions.get(i).getVoucherNo());
//                    obj.put("ORDERTYPE", orderTransactions.get(i).getOrderType());
//                    obj.put("TABLENO", orderTransactions.get(i).getTableNo());
//                    obj.put("SECTIONNO", orderTransactions.get(i).getSectionNo());
//                    if(orderTransactions.get(i).getOrderKind()==1){
//                        obj.put("ISUPDATE", 1);
//                        Log.e("ISUPDATE  =1",""+orderTransactions.get(i).getOrderKind());}
//                    else if(orderTransactions.get(i).getOrderKind()==0){
//                        obj.put("ISUPDATE", 0);
//                        Log.e("ISUPDATE =0",""+orderTransactions.get(i).getOrderKind());}
//
//                    obj.put("CASHNO", orderTransactions.get(i).getCashNo());
//                    objNo.put(obj);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        Log.e("getobj " + KitchenNo, "" + objNo.toString());
//
//        return objNo;
//    }

    public boolean checkHosts(String subnet) {
        int timeout = 1000;
        boolean fa = false;
        try {
            if (InetAddress.getByName(subnet).isReachable(timeout)) {
                System.out.println(subnet + " is reachable");
                fa = true;

            }
        } catch (IOException e) {
            e.printStackTrace();
            fa = false;
        }
        Log.e("tesr3", "fa ==>" + fa);
        return fa;
    }



}
