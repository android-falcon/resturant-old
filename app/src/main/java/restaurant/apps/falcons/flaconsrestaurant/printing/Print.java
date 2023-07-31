package restaurant.apps.falcons.flaconsrestaurant.printing;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import restaurant.apps.falcons.flaconsrestaurant.R;
import restaurant.apps.falcons.flaconsrestaurant.activities.InvoiceActivity;
import restaurant.apps.falcons.flaconsrestaurant.models.Item;
import restaurant.apps.falcons.flaconsrestaurant.models.Table;
import restaurant.apps.falcons.flaconsrestaurant.models.User;
import restaurant.apps.falcons.flaconsrestaurant.util.Constants;
import restaurant.apps.falcons.flaconsrestaurant.util.DataManager;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ahmad-alsaleh on 02/02/17.
 */

public class Print
{

    public static void printReceiptArabic(Table table, String type, Context context, int status) throws IOException
    {
        table.calcTotal();

        StringBuilder builder = new StringBuilder();
        String line1 = DataManager.getRegisters().get("PRINT_LINE_1");
        String line2 = DataManager.getRegisters().get("PRINT_LINE_2");
        String line3 = DataManager.getRegisters().get("PRINT_LINE_3");

        builder.append("\n\n");
        if (line1 != null && line1.length() > 0)
        {
            builder.append(line1).append("\n");
        }
        if (line2 != null && line2.length() > 0)
        {
            builder.append(line2).append("\n");
        }
        if (line3 != null && line3.length() > 0)
        {
            builder.append(line3).append("\n");
        }
        builder.append(type.equals("statement") ? "طاولة" + "                " + table.getId() + " " : "سفري");
        builder.append("\n");
        builder.append(DataManager.getSections().get(table.getSection())).append("                ");
        builder.append("\n");
        builder.append("فاتورة").append("                             ").append(table.getOrderID()).append("  ").append("#");
        builder.append("\n");
        builder.append("    ").append("اشخاص");
        builder.append(new SimpleDateFormat("dd/MM/yyyy    HH:mm:ss", Locale.ENGLISH).format(table.getDate())).append("        ").append(table.getSeatCount()).append("  ").append("#").append("\n");
        builder.append("الكابتن").append(DataManager.getUsers().get(table.getUser()).getName()).append("       ").append("\n");

        String line = new String(new char[40]).replace("\0", "-");

        builder.append(line).append("\n");
        builder.append("وصفe المادة       سعر     كميه  \n");
        builder.append(line).append("\n");

        SharedPreferences sp = Constants.getSharedPrefs(context);
        String language = sp.getString(Constants.language, "en");


        for (Item item : table.getItems())
        {
            String desc = language.equals("ar") ? item.getName() : item.getName2();
            if (desc.length() > 25)
            {
                desc = desc.substring(0, 25);
            }
            builder.append(String.format(Locale.ENGLISH, "%25s", desc));
            builder.append("  ");
            builder.append(String.format(Locale.ENGLISH, "%-6s", getNumber(item.getQty())));
            builder.append("   ");
            String total = String.format(Locale.ENGLISH, "%.3f", item.getPrice() * item.getQty());
            builder.append(String.format(total, "%-8s"));
            builder.append("     ");

            builder.append("\n");
        }

        builder.append(line).append("\n");
        builder.append("اجمالي").append("    ").append(String.format(Locale.ENGLISH, "%.3f", table.getTotal() + table.getDiscount() - table.getService())).append("    ").append("\n");
        builder.append("خدمه").append("    ").append(String.format(Locale.ENGLISH, "%.3f", table.getService())).append("    ").append("\n");
        builder.append("خصم").append("    ").append(String.format(Locale.ENGLISH, "%.3f", table.getDiscount())).append("    ").append("\n");
        builder.append("ضريبه").append("    ").append(String.format(Locale.ENGLISH, "%.3f", table.getTax())).append("    ").append("\n");
        builder.append(new String(new char[20]).replace("\0", "-")).append("\n");
        builder.append("الصافي").append("    ").append(String.format(Locale.ENGLISH, "%.3f", table.getTotal() + table.getTax())).append("    ");

        String text = builder.toString().replace("\\n", "\n").replace("\n", "\n");
        System.out.print(text);
        String x = "خدمة";
        char c = x.charAt(3);

        printInvoice(text, context, table.getId(), "-1", status);
    }

    public static void printReceiptEnglish(Table table, String type, Context context, int status) throws IOException
    {
        table.calcTotal();

        StringBuilder builder = new StringBuilder();
        String line1 = DataManager.getRegisters().get("PRINT_LINE_1");
        String line2 = DataManager.getRegisters().get("PRINT_LINE_2");
        String line3 = DataManager.getRegisters().get("PRINT_LINE_3");


        builder.append("\n\n");
        if (line1 != null && line1.length() > 0)
        {
            builder.append(line1).append("\n");
        }
        if (line2 != null && line2.length() > 0)
        {
            builder.append(line2).append("\n");
        }
        if (line3 != null && line3.length() > 0)
        {
            builder.append(line3).append("\n");
        }
        builder.append(type.equals("statement") ? "Table " + table.getId() : "Take Out");
        builder.append("\n");
        builder.append("\n");
        builder.append(DataManager.getSections().get(table.getSection()));
        builder.append("\n");
        builder.append("Trans #       ").append(table.getOrderID());
        builder.append("\n");
        builder.append(new SimpleDateFormat("dd/MM/yyyy    HH:mm:ss", Locale.ENGLISH).format(table.getDate()));
        builder.append("        # Cust    ").append(table.getSeatCount()).append("\n");
        builder.append("Captain       ").append(DataManager.getUsers().get(table.getUser()).getName()).append("\n");

        String line = new String(new char[40]).replace("\0", "-");

        builder.append(line).append("\n");
        builder.append("Qty   Description              Price\n");
        builder.append(line).append("\n");

        SharedPreferences sp = Constants.getSharedPrefs(context);
        String language = sp.getString(Constants.language, "en");

        for (Item item : table.getItems())
        {
            String desc = language.equals("en") ? item.getName() : item.getName2();
            if (desc.length() > 25)
            {
                desc = desc.substring(0, 25);
            }
            builder.append(String.format(Locale.ENGLISH, "%-6s", getNumber(item.getQty())));
            builder.append(String.format(Locale.ENGLISH, "%-25s", desc));

            String total = String.format(Locale.ENGLISH, "%.3f", item.getPrice() * item.getQty());
            builder.append(String.format(total, "%8s"));
            builder.append("\n");
        }

        builder.append(line).append("\n");
        builder.append("SubTotal    ").append(String.format(Locale.ENGLISH, "%.3f", table.getTotal() + table.getDiscount() - table.getService())).append("\n");
        builder.append("Service     ").append(String.format(Locale.ENGLISH, "%.3f", table.getService())).append("\n");
        builder.append("Discount    ").append(String.format(Locale.ENGLISH, "%.3f", table.getDiscount())).append("\n");
        builder.append("Tax         ").append(String.format(Locale.ENGLISH, "%.3f", table.getTax())).append("\n");
        builder.append(new String(new char[20]).replace("\0", "-")).append("\n");
        builder.append("TOTAL    ").append(String.format(Locale.ENGLISH, "%.3f", table.getTotal() + table.getTax()));

        String text = builder.toString().replace("\\n", "\n").replace("\n", "     \n");
        printInvoice(text, context, table.getId(), "-1", status);
    }

    private static String getNumber(float mFloat)
    {
        if (mFloat - (int) mFloat != 0)
            return mFloat + "";
        else
            return ((int) mFloat) + "";
    }

    private static void doPrint(String text, Context context, String tableId) throws IOException
    {
        BluetoothAdapter BA;
        Set<BluetoothDevice> pairedDevices;
        BA = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = BA.getBondedDevices();
        for (BluetoothDevice device : pairedDevices)
        {
            if (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.IMAGING || device.getName().toLowerCase().contains("printer"))
            {
                try
                {
                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
                    socket.connect();
                    ArabicPrint pr = new ArabicPrint();
                    OutputStream writer = socket.getOutputStream();
                    writer.write(pr.Get_printerCode(text, context, 6));
                    socket.close();

                    // update table status
                    DataManager.DoQuery("update TABLES set TBLSTATUS='2' where TBLNO='" + tableId + "'");

                    break;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void printInvoice(final String text, final Context context, final String tableId, String password, final int status)
    {
        if (status == 2)
        {
            User user = DataManager.getUsers().get(password);
            if (user != null && user.getMaster().equals("1"))
            {
                try
                {
                    doPrint(text, context, tableId);
                    InvoiceActivity activity = (InvoiceActivity) context;
                    activity.saveInvoice();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                ((Activity) context).runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(context.getString(R.string.already_printed));

                        View pwView = LayoutInflater.from(context).inflate(R.layout.password_edittext, null);
                        builder.setView(pwView);

                        final EditText pw = (EditText) pwView.findViewById(R.id.password_edittext);
                        pw.setHint(context.getString(R.string.password));
                        builder.setPositiveButton(context.getString(R.string.print), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        super.run();

                                        printInvoice(text, context, tableId, pw.getText().toString(), status);
                                    }
                                }.start();
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, null);
                        builder.create().show();
                    }
                });
            }
        }
        else
        {
            try
            {
                doPrint(text, context, tableId);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
