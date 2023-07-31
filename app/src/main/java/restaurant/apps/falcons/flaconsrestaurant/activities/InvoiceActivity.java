package restaurant.apps.falcons.flaconsrestaurant.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import cn.pedant.SweetAlert.SweetAlertDialog;
import restaurant.apps.falcons.flaconsrestaurant.R;
import restaurant.apps.falcons.flaconsrestaurant.adapters.CategoriesAdapter;
import restaurant.apps.falcons.flaconsrestaurant.adapters.ExpandableListAdapter;
import restaurant.apps.falcons.flaconsrestaurant.adapters.ItemsListViewAdapter;
import restaurant.apps.falcons.flaconsrestaurant.adapters.ModifiersAdapter;
import restaurant.apps.falcons.flaconsrestaurant.models.*;
import restaurant.apps.falcons.flaconsrestaurant.printing.Print;
import restaurant.apps.falcons.flaconsrestaurant.util.Constants;
import restaurant.apps.falcons.flaconsrestaurant.util.DataManager;
import restaurant.apps.falcons.flaconsrestaurant.util.SendSocket;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static restaurant.apps.falcons.flaconsrestaurant.util.Constants.DEVICE_TYPE_TAKE_OUT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * Created by Salah on 7/30/2016.
 */

public class InvoiceActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRef,getDataRef ;
    private MediaPlayer mediaPlayer;
    private GridView gridView;
    private List<DummyItem> dummyItemList;
    public static Table[] currentTable = {null};
    private ExpandableListAdapter expandableListAdapter;
    private ExpandableListView expandableListView;
    private TextView ordNoTextView;
    private TextView sectionTextView;
    private TextView totalTextView;
    private TextView taxTextView;
    private TextView netTextView;
    private TextView dateTextView;
    private TextView userTextView;
    private EditText seatsEditText;
    private TextView tableTextView;
    private ListView itemsListView;
    private ItemsListViewAdapter itemsListViewAdapter;
    private LinearLayout itemsLayout;
    private boolean isVisible = false;
    private TextView clearItems;
    private boolean hideModifierRoot;
    private final SimpleDateFormat mySdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private void unlockTable() {
        new Thread() {
            @Override
            public void run() {
                Log.e("unlockTable",""+currentTable[0].getOrderID());
                DataManager.unlockTable(currentTable[0]);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unlockTable();
        finish();

    }





    public void saveInvoice() {

        boolean isTakeOut = DataManager.getDeviceType(InvoiceActivity.this) == DEVICE_TYPE_TAKE_OUT;
        if (!isTakeOut) {
            finishDineIn();
        } else {


            final ProgressDialog pb = new ProgressDialog(InvoiceActivity.this);
            pb.setMessage(getString(R.string.processing));
            pb.setCancelable(false);
            pb.show();

            new Thread() {
                @Override
                public void run() {
                    super.run();


                    final boolean success = DataManager.saveTable(currentTable[0], InvoiceActivity.this);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Resources r = getResources();
                            float t1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, r.getDisplayMetrics());

                            Toast toast;
                            if (!success) {
                                toast = Toast.makeText(InvoiceActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT);
                            } else {
                                toast = Toast.makeText(InvoiceActivity.this, getString(R.string.payment_success), Toast.LENGTH_SHORT);
                            }

                            ViewGroup group = (ViewGroup) toast.getView();
                            TextView toastMsg = (TextView) group.getChildAt(0);
                            toastMsg.setTextSize(t1);
                            toast.show();


                            if (success)
                            {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pb.dismiss();
                                        finish();
                                    }
                                }, 5000);
                            }
                            else
                                pb.dismiss();



                        }
                    });
                }
            }.start();



     /*       AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceActivity.this);
            builder.setTitle(R.string.select_payment_method);

            View paymentsView = LayoutInflater.from(InvoiceActivity.this).inflate(R.layout.payment_methods, null);
            builder.setView(paymentsView);

            final EditText cashEd = (EditText) paymentsView.findViewById(R.id.cash_ed);
            final EditText visaEd = (EditText) paymentsView.findViewById(R.id.visa_ed);
            final EditText masterEd = (EditText) paymentsView.findViewById(R.id.master_card_ed);
            final EditText otherEd = (EditText) paymentsView.findViewById(R.id.other_ed);

            TextView one = (TextView) paymentsView.findViewById(R.id.one);
            TextView five = (TextView) paymentsView.findViewById(R.id.five);
            TextView ten = (TextView) paymentsView.findViewById(R.id.ten);
            TextView twenty = (TextView) paymentsView.findViewById(R.id.twenty);
            TextView fifty = (TextView) paymentsView.findViewById(R.id.fifty);
            TextView exact = (TextView) paymentsView.findViewById(R.id.exact);

            final TextView totalAmount = (TextView) paymentsView.findViewById(R.id.total_amount);
            final TextView payedAmount = (TextView) paymentsView.findViewById(R.id.payed_amount);
            final TextView changeAmount = (TextView) paymentsView.findViewById(R.id.change_amount);

            payedAmount.setText("0.000");
            changeAmount.setText("0.000");

            double totalAm = currentTable[0].getTotal() + currentTable[0].getTax();
            if (Double.isNaN(totalAm))
                totalAm = 0;

            totalAmount.setText(String.format(Locale.ENGLISH, "%.3f", totalAm));
            final double totalFinal = totalAm;

            one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String cashStr = cashEd.getText().toString();
                    if (!cashStr.isEmpty()) {
                        double curCash = Double.parseDouble(cashStr);
                        curCash = curCash + 1;
                        cashEd.setText(curCash + "");
                    } else {
                        cashEd.setText(1.000 + "");
                    }
                }
            });

            five.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String cashStr = cashEd.getText().toString();
                    if (!cashStr.isEmpty()) {
                        double curCash = Double.parseDouble(cashStr);
                        curCash = curCash + 5;
                        cashEd.setText(curCash + "");
                    } else {
                        cashEd.setText(5.000 + "");
                    }
                }
            });

            ten.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String cashStr = cashEd.getText().toString();
                    if (!cashStr.isEmpty()) {
                        double curCash = Double.parseDouble(cashStr);
                        curCash = curCash + 10;
                        cashEd.setText(curCash + "");
                    } else {
                        cashEd.setText(10.000 + "");
                    }
                }
            });

            twenty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String cashStr = cashEd.getText().toString();
                    if (!cashStr.isEmpty()) {
                        double curCash = Double.parseDouble(cashStr);
                        curCash = curCash + 20;
                        cashEd.setText(curCash + "");
                    } else {
                        cashEd.setText(20.000 + "");
                    }
                }
            });

            fifty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String cashStr = cashEd.getText().toString();
                    if (!cashStr.isEmpty()) {
                        double curCash = Double.parseDouble(cashStr);
                        curCash = curCash + 50;
                        cashEd.setText(curCash + "");
                    } else {
                        cashEd.setText(50.000 + "");
                    }
                }
            });

            exact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cashEd.setText(totalFinal + "");
                }
            });

            cashEd.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    calPayedAmount(cashEd, visaEd, masterEd, otherEd, totalFinal, payedAmount, changeAmount);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            visaEd.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    calPayedAmount(cashEd, visaEd, masterEd, otherEd, totalFinal, payedAmount, changeAmount);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            masterEd.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    calPayedAmount(cashEd, visaEd, masterEd, otherEd, totalFinal, payedAmount, changeAmount);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            otherEd.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    calPayedAmount(cashEd, visaEd, masterEd, otherEd, totalFinal, payedAmount, changeAmount);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (totalFinal != 0 && currentTable[0].getPayed() >= totalFinal) {
                        final ProgressDialog pb = new ProgressDialog(InvoiceActivity.this);
                        pb.setMessage(getString(R.string.processing));
                        pb.setCancelable(false);
                        pb.show();

                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                final boolean success = DataManager.saveTable(currentTable[0], InvoiceActivity.this);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Resources r = getResources();
                                        float t1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, r.getDisplayMetrics());

                                        Toast toast;
                                        if (!success) {
                                            toast = Toast.makeText(InvoiceActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT);
                                        } else {
                                            toast = Toast.makeText(InvoiceActivity.this, getString(R.string.payment_success), Toast.LENGTH_SHORT);
                                        }

                                        ViewGroup group = (ViewGroup) toast.getView();
                                        TextView toastMsg = (TextView) group.getChildAt(0);
                                        toastMsg.setTextSize(t1);
                                        toast.show();

                                        pb.dismiss();
                                        if (success)
                                            finish();
                                    }
                                });
                            }
                        }.start();
                    } else {
                        Toast.makeText(InvoiceActivity.this, getString(R.string.all_info_are_correct), Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel, null);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();*/
        }
    }

    SharedPreferences sp ;
    String timeItem ,callCaptinValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String hString = DataManager.getRegisters().get("HIDE_MODFER_ROOT");
        if (hString != null && hString.equals("1")) {
            hideModifierRoot = true;
        }

         sp = Constants.getSharedPrefs(this);
        String language = sp.getString(Constants.language, "en");
        timeItem= sp.getString(Constants.work_withTime, "");
        callCaptinValue=sp.getString(Constants.call_captin, "");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Captin");
        Locale locale;
        if (language.equals("ar"))
            locale = new Locale("ar");
        else
            locale = new Locale("en_US");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, null);

        setContentView(R.layout.invoice_activity);

        dateTextView = (TextView) findViewById(R.id.date_value);

        ordNoTextView = (TextView) findViewById(R.id.order_num_value);
        sectionTextView = (TextView) findViewById(R.id.section_value);
        totalTextView = (TextView) findViewById(R.id.total_value);
        taxTextView = (TextView) findViewById(R.id.tax_value);
        netTextView = (TextView) findViewById(R.id.net_total_value);
        userTextView = (TextView) findViewById(R.id.user_value);
        tableTextView = (TextView) findViewById(R.id.table_num_value);

        seatsEditText = (EditText) findViewById(R.id.seats_count_value);
        itemsListView = (ListView) findViewById(R.id.items_list_view);
        itemsLayout = (LinearLayout) findViewById(R.id.items_layout);

        gridView = (GridView) findViewById(R.id.items_gridview);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableList);

        setGridAdapterToCategories();

        Button backView = (Button) findViewById(R.id.items_back);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGridAdapterToCategories();
            }
        });

        gridView.requestFocus();

        // currentTable[0] = new Table();
        expandableListAdapter = new ExpandableListAdapter(InvoiceActivity.this, InvoiceActivity.this);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                if (i == expandableListAdapter.getSelectedIndex()) {
                    setItemNote(currentTable[0].getItems().get(i));
                    return true;
                }
                expandableListAdapter.setSelectedIndex(i);
                expandableListAdapter.setSelectedChildIndex(-1);
                expandableListAdapter.notifyDataSetChanged();
                return true;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                expandableListAdapter.setSelectedIndex(i);
                expandableListAdapter.setSelectedChildIndex(i1);
                expandableListAdapter.notifyDataSetChanged();
                return true;
            }
        });

        final ImageView printButton = (ImageView) findViewById(R.id.print);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printButton.setEnabled(false);
                new Thread() {
                    @Override
                    public void run() {

                        try {
                            Table table = currentTable[0];
                            String printLang = DataManager.getRegisters().get("PRINT_LANG") + "";
                            String printBluetooth = DataManager.getRegisters().get("PRINT_BLUETOOTH") + "";
                            int status = DataManager.getTableStatus(table.getId());
                            if (status == 2) {

                                if (printLang.toLowerCase().equals("ar"))
                                    Print.printReceiptArabic(table, "statement", InvoiceActivity.this, status);
                                else
                                    Print.printReceiptEnglish(table, "statement", InvoiceActivity.this, status);
                            } else {
                                saveInvoice();

                                if (printBluetooth.equals("1")) {
                                    if (printLang.toLowerCase().equals("ar"))
                                        Print.printReceiptArabic(table, "statement", InvoiceActivity.this, status);
                                    else
                                        Print.printReceiptEnglish(table, "statement", InvoiceActivity.this, status);
                                } else {
                                    //DataManager.printInvoice(InvoiceActivity.this, table);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //DataManager.sendUDP(currentTable[0], InvoiceActivity.this);
                    }
                }.start();
            }
        });

        printButton.setVisibility(View.INVISIBLE);

        Button modifierTV = (Button) findViewById(R.id.modf);

        Button stopItemBtn = (Button) findViewById(R.id.items_stop);
        Button callCaptin_btn= (Button) findViewById(R.id.callCaptin_btn);
        Log.e("timeItem","="+timeItem);
        if(timeItem.equals("1")){
            stopItemBtn.setVisibility(View.VISIBLE);

        }else {
            stopItemBtn.setVisibility(View.GONE);
        }

        if(callCaptinValue.equals("1")){
            callCaptin_btn.setVisibility(View.VISIBLE);

        }else {
            callCaptin_btn.setVisibility(View.GONE);
        }
        callCaptin_btn.setOnClickListener(v->{

            callCaptin();

//            SendSocket sendSocket = new SendSocket(this);
//            sendSocket.sendMessage(tableTextView.getText().toString(),seatsEditText.getText().toString(),dateTextView.getText().toString());
        });
        stopItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = expandableListAdapter.getSelectedIndex();
                int childIndex = expandableListAdapter.getSelectedChildIndex();
                if (index == -1) {
                    return;
                }
                final Item item = expandableListAdapter.getItems().get(index);

                if(item.isTimeItem())
                {
                    if(item.getIsFinishedTime() == 1)
                    {
                        Toast.makeText(getApplicationContext(), R.string.finished_time_item, Toast.LENGTH_SHORT).show();
                    }else{

                        Date endDate = new Date();

                        String dateString = mySdf.format(endDate);
                        try {
                            endDate = mySdf.parse(dateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long difference = endDate.getTime() - item.getOpenTime().getTime();
                        long seconds = difference / 1000;
                        final long minutes = seconds / 60;

                        if (minutes >= 1)
                        {
                            AlertDialog.Builder alert = new AlertDialog.Builder(InvoiceActivity.this);
                            alert.setTitle(getResources().getString(R.string.stop_time_item_t));
                            alert.setMessage(getResources().getString(R.string.stop_time_item));

                            final Date finalEndDate = endDate;
                            alert.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    item.setQty(minutes);
                                    item.setEndTime(finalEndDate);
                                    item.setIsFinishedTime(1);
                                    expandableListAdapter.notifyDataSetChanged();
                                    setTotals();

                                    Toast.makeText(InvoiceActivity.this, R.string.item_stoped,
                                            Toast.LENGTH_LONG).show();

                                    dialog.dismiss();
                                }
                            });

                            alert.setNegativeButton(R.string.no_msg, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            alert.show();
                        }
                        else
                            Toast.makeText(getApplicationContext(), R.string.must_wait, Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(), R.string.not_time_item, Toast.LENGTH_SHORT).show();
                }
            }
        });

        modifierTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = expandableListAdapter.getSelectedIndex();
                int childIndex = expandableListAdapter.getSelectedChildIndex();
                if (index == -1) {
                    return;
                }
                Item item = expandableListAdapter.getItems().get(index);

                if (index > -1 && !item.isOld() && childIndex == -1) {
                    Category category = DataManager.getCategories().get(item.getCategory());
                    if (category != null) {
                        if (category.getModifiers().size() > 0) {
                            HashMap<String, Modifier> usedModifiers = new HashMap<>();
                            for (Modifier modifier : item.getOrgModifiers()) {
                                usedModifiers.put(modifier.getId(), modifier);
                            }
                            item.getOrgModifiers().clear();
                            for (Modifier modifier : category.getModifiers()) {
                                if (usedModifiers.containsKey(modifier.getId())) {
                                    item.getOrgModifiers().add(usedModifiers.get(modifier.getId()));
                                } else {
                                    Modifier newModifier = new Modifier();
                                    newModifier.setId(modifier.getId());
                                    newModifier.setDesc(modifier.getDesc());
                                    newModifier.setType("");
                                    item.getOrgModifiers().add(newModifier);
                                }
                            }
                            setModifiers(item.getOrgModifiers(), item.getModifiers());
                        }
                    }

                }
            }
        });


        Button noteButton = (Button) findViewById(R.id.table_note);
        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNote();
            }
        });

        Button voidButton = (Button) findViewById(R.id.table_void);
        voidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voidItem(0);
            }
        });

        Button cancelButton = (Button) findViewById(R.id.table_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voidItem(1);
            }
        });

        Button exitButton = (Button) findViewById(R.id.exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //unlockTable();
                finish();
            }
        });

        ImageView sync = (ImageView) findViewById(R.id.sync);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVisible) {
                    isVisible = false;
                    itemsLayout.startAnimation(outToLeftAnimation());

                    android.os.Handler handler1 = new android.os.Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            itemsLayout.setVisibility(View.INVISIBLE);
                        }
                    }, 500);

                } else {
                    isVisible = true;
                    itemsLayout.startAnimation(inFromLeftAnimation());

                    android.os.Handler handler1 = new android.os.Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            itemsLayout.setVisibility(View.VISIBLE);
                        }
                    }, 500);
                }
            }
        });

        sync.setVisibility(View.INVISIBLE);

        TextView hideItems = (TextView) findViewById(R.id.hide_items);
        hideItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVisible = false;
                itemsLayout.startAnimation(outToLeftAnimation());

                android.os.Handler handler1 = new android.os.Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        itemsLayout.setVisibility(View.INVISIBLE);
                    }
                }, 500);
            }
        });

        clearItems = (TextView) findViewById(R.id.clear_items);
        clearItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceActivity.this);
                builder.setTitle(R.string.caution);
                builder.setMessage(R.string.delete_items_message);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                boolean success = DataManager.clearTableItems(currentTable[0].getId());
                                if (success) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            clearItems.setVisibility(View.INVISIBLE);
                                            itemsListViewAdapter = new ItemsListViewAdapter(InvoiceActivity.this, new ArrayList<Item>());
                                            itemsListView.setAdapter(itemsListViewAdapter);
                                        }
                                    });
                                } else {
                                    Toast.makeText(InvoiceActivity.this, R.string.unable_to_connect, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }.start();
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.create().show();
            }
        });

        (new GetTableOrder()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        final Button finishButton = (Button) findViewById(R.id.table_finish);
        boolean isTakeOut = DataManager.getDeviceType(this) == DEVICE_TYPE_TAKE_OUT;
        if (isTakeOut)
            finishButton.setText(getString(R.string.pay));
        else
        {
            finishButton.setText(R.string.finish);
                 }

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishButton.setEnabled(false);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            runOnUiThread(new TimerTask() {
                                @Override
                                public void run() {
                                    finishButton.setEnabled(true);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 3000);
                saveInvoice();
            }
        });

        setTotals();
        for (int x = 0; x < currentTable[0].getItems().size(); x++) {
            expandableListView.expandGroup(x);
        }

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                  /*  if (DataManager.isTableLocked(InvoiceActivity.this, currentTable[0], true)) {

                        runOnUiThread(new TimerTask() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), getString(R.string.tableBlocked), Toast.LENGTH_SHORT).show();
                            }
                        });

                        finish();
                    }*/
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        Table table = currentTable[0];
        seatsEditText.setText(String.valueOf(table.getSeatCount()));

        try
        {
            userTextView.setText(DataManager.getUsers().get(table.getUser()).getName());
        }catch (Exception e)
        {
            userTextView.setText("Un Defined!");
        }
        //userTextView.setText(DataManager.getUsers().get(table.getUser()).getName());

        if (isTakeOut) {
            tableTextView.setText(R.string.take_out);
        } else {
            tableTextView.setText(table.getId());
        }

        sectionTextView.setText(table.getSection());
        ordNoTextView.setText(table.getOrderID());
        dateTextView.setText(sdf.format(table.getDate()));


    }

    private void callCaptin() {
        Log.e("callll","captin");
        mediaPlayer = MediaPlayer.create(this, R.raw.bell);
        mediaPlayer.start();
//        count++;,
        TableInfoModel tableInfoModel=new TableInfoModel();
        tableInfoModel.setTableNo(tableTextView.getText().toString()+"");
        tableInfoModel.setSeats(seatsEditText.getText().toString());
        tableInfoModel.setDoneOrder("0");
        tableInfoModel.setDateOrder(dateTextView.getText().toString());
        add(tableInfoModel);
    }
    public void add(TableInfoModel tableInfo) {
        String ipValue= sp.getString(Constants.ipAddress,"").replace(".", "_");
       Log.e("ipValue","="+ipValue);
        myRef.child(ipValue).child(tableInfo.getTableNo()+"").setValue(tableInfo).addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(InvoiceActivity.this, "Customer data has been sent to the Admin successfully", Toast.LENGTH_SHORT).show();

            }
        });
        myRef.child(ipValue).child(tableInfo.getTableNo()+"").setValue(tableInfo).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InvoiceActivity.this, "Failled  sent  data to the Admin ", Toast.LENGTH_SHORT).show();

            }
        });

    }
    private void calPayedAmount(EditText cashEd, EditText visaEd, EditText masterEd, EditText otherEd, double totalAmount, TextView payedAmount, TextView changeAmount) {
        String cashStr = cashEd.getText().toString();
        String visaStr = visaEd.getText().toString();
        String masterStr = masterEd.getText().toString();
        String otherStr = otherEd.getText().toString();

        double cash = 0;
        double visa = 0;
        double master = 0;
        double other = 0;

        if (!cashStr.equals("")) {
            cash = Double.parseDouble(cashEd.getText().toString());
        }
        if (!visaStr.equals("")) {
            visa = Double.parseDouble(visaEd.getText().toString());
        }
        if (!masterStr.equals("")) {
            master = Double.parseDouble(masterEd.getText().toString());
        }
        if (!otherStr.equals("")) {
            other = Double.parseDouble(otherEd.getText().toString());
        }

        double payed = cash + visa + master + other;
        payedAmount.setText(String.format(Locale.ENGLISH, "%.3f", payed));

        double change = payed - totalAmount;
        if (cashStr.equals("") && visaStr.equals("") && masterStr.equals(""))
            changeAmount.setText("0.000");
        else
            changeAmount.setText(String.format(Locale.ENGLISH, "%.3f", change));

        currentTable[0].setCash(cash);
        currentTable[0].setChange(change);
        currentTable[0].setPayed(payed);
        currentTable[0].setVisa(visa);
        currentTable[0].setMaster(master);
        currentTable[0].setOther(other);
    }

    private void finishDineIn() {



        String sCount = seatsEditText.getText().toString();
        if (sCount.length() == 0) {
            sCount = "0";
        }
        currentTable[0].setSeatCount(Integer.valueOf(sCount));

        if (currentTable[0].getItems().size() == 0) {
            Toast.makeText(InvoiceActivity.this, "Empty Invoice Is Not Allowed", Toast.LENGTH_LONG).show();
            return;
        }
        if (DataManager.getRegisters().get("FORCE_SELECT_SEAT_NO").equals("1") && currentTable[0].getSeatCount() < 1) {
            Toast.makeText(InvoiceActivity.this, "Set Seat Count", Toast.LENGTH_LONG).show();
            return;
        }
        //        final ProgressDialog progressDialog = new ProgressDialog(InvoiceActivity.this);
        //        progressDialog.setTitle(getString(R.string.please_wait));
        //        progressDialog.setMessage(getString(R.string.loading_data));
        //        progressDialog.show();



        new Thread() {
            @Override
            public void run() {
//??????
               /* if (DataManager.isTableLocked(InvoiceActivity.this, currentTable[0], true)) {
//                    Toast.makeText(getApplicationContext(), getString(R.string.tableBlocked), Toast.LENGTH_SHORT).show();
                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.tableBlocked, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    });
                    return;
                }*/

                SharedPreferences sp = Constants.getSharedPrefs(InvoiceActivity.this);
                String server = sp.getString(Constants.ipAddress, "");
                int port = 8085;
                if (!DataManager.pingHost(server, port, 5000)) {
                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            //                            if (progressDialog.isShowing()) {
                            //                                progressDialog.dismiss();
                            //                            }
                            Toast.makeText(getApplicationContext(), R.string.unable_to_connect, Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }


                boolean saved = DataManager.saveTable(currentTable[0], InvoiceActivity.this);
                if (!saved) {
                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            Toast.makeText(InvoiceActivity.this, "Order not saved, try again", Toast.LENGTH_LONG).show();
                            //          progressDialog.dismiss();
                        }
                    });
                    return;
                }


                DataManager.sendUDP(currentTable[0], InvoiceActivity.this);
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        unlockTable();
                        finish();
                    }
                });

            }
        }.start();




    }

    private void voidItem(final int type) {

        if (currentTable[0].getItems().size() == 0) {
            return;
        }
        int index = expandableListAdapter.getSelectedIndex();
        if (index == -1 && type == 0) {
            return;
            //     index = currentTable[0].getItems().size() - 1;
        }
        if (currentTable[0].getItems().size() > index) {
            if (index == -1) {
                index = 0;
            }
            Item item = currentTable[0].getItems().get(index);
            if (!item.isOld()) {
                executeVoid(type, null);
                return;
            }
        }

        if (!DataManager.getCurrentUser(InvoiceActivity.this).getMaster().equals("1"))
            return;
        final List<RefundReason> reasons = DataManager.getRefundReasons();
        if (reasons.size() == 0)
            return;
        String[] reasonsArray = new String[reasons.size()];
        for (int i = 0; i < reasons.size(); i++) {
            reasonsArray[i] = reasons.get(i).getDesc();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceActivity.this);
        builder.setTitle(R.string.reason);
        final int[] selectedIndex = {0};
        builder.setSingleChoiceItems(reasonsArray, selectedIndex[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedIndex[0] = i;
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                RefundReason refundReason = reasons.get(selectedIndex[0]);
                executeVoid(type, refundReason);
            }
        });
        builder.setNegativeButton(R.string.back, null);
        builder.show();
    }

    private void executeVoid(int type, RefundReason refundReason) {
        List<Item> items = currentTable[0].getItems();
        if (items.size() > 0) {
            if (type == 0) {
                int sIndex = expandableListAdapter.getSelectedIndex();
                int cIndex = expandableListAdapter.getSelectedChildIndex();
                int index = sIndex > -1 ? sIndex : items.size() - 1;
                Item item = items.get(index);
                if (cIndex < item.getModifiers().size() && cIndex != -1) {
                    item.getModifiers().remove(cIndex);
                } else {
                    item.setRefundReason(refundReason);
                    items.remove(item);
                    if (refundReason != null) {
                        currentTable[0].getRefundItems().add(item);
                        for (Answer answer : item.getAnswers()) {
                            if (answer.getItem() != null) {
                                currentTable[0].getRefundItems().add(answer.getItem());
                                answer.getItem().setForceQ(true);
                                answer.getItem().setRefundReason(refundReason);
                                answer.getItem().setOld(item.isOld());
                            }
                        }
                    }
                }
            } else {
                for (Item item : items) {
                    item.setRefundOrCancel(true);
                    item.setRefundReason(refundReason);
                    currentTable[0].getRefundItems().add(item);
                    for (Answer answer : item.getAnswers()) {
                        if (answer.getItem() != null) {
                            currentTable[0].getRefundItems().add(answer.getItem());
                            answer.getItem().setForceQ(true);
                            answer.getItem().setRefundReason(refundReason);
                            answer.getItem().setOld(item.isOld());
                        }
                    }
                }
                items.clear();
            }
            expandableListAdapter = new ExpandableListAdapter(InvoiceActivity.this, InvoiceActivity.this);
            expandableListView.setAdapter(expandableListAdapter);

            int count = expandableListAdapter.getGroupCount();
            for (int i = 0; i < count; i++)
                expandableListView.expandGroup(i);
            expandableListView.setSelectedGroup(expandableListAdapter.getGroupCount() - 1);

            setTotals();
        }
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

    public void setTotals() {
        Table table = currentTable[0];
        table.calcTotal();

        totalTextView.setText(String.format(Locale.ENGLISH, "%.3f", table.getTotal()));
        taxTextView.setText(String.format(Locale.ENGLISH, "%.3f", table.getTax()));
        netTextView.setText(String.format(Locale.ENGLISH, "%.3f", table.getTotal() + table.getTax()));

    }

    private void setNote() {
        if (!DataManager.getRegisters().get("SHOW_TABLE_REMARK").equals("1"))
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceActivity.this);
        builder.setTitle(R.string.table_note);
        final EditText editText = new EditText(InvoiceActivity.this);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80));
        editText.setText(currentTable[0].getNote());
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        builder.setView(editText);
        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                currentTable[0].setNote(editText.getText().toString());
            }
        });
        builder.setNegativeButton(getString(R.string.back), null);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        unlockTable();
        finish();
    }

    private void setModifiers(final List<Modifier> orgModifiers, final List<Modifier> modifiers) {
        View view = getLayoutInflater().inflate(R.layout.modifiers_view, null);
        final GridView gridView = (GridView) view.findViewById(R.id.grid);
        final String[] selectedAction = {""};

        Button hfButton = (Button) view.findViewById(R.id.hfButton);
        Button ltButton = (Button) view.findViewById(R.id.ltButton);
        Button noButton = (Button) view.findViewById(R.id.noButton);
        Button extButton = (Button) view.findViewById(R.id.extButton);

        hfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedAction[0] = "HF";
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedAction[0] = "NO";
            }
        });

        ltButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedAction[0] = "LT";
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedAction[0] = "NO";
            }
        });

        extButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedAction[0] = "EXT";
            }
        });

        final BaseAdapter adapter = new ModifiersAdapter(orgModifiers, InvoiceActivity.this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (selectedAction[0].length() == 0) {
                    return;
                }
                Modifier modifier = orgModifiers.get(i);
                modifier.setType(selectedAction[0]);

                Modifier nModifier = new Modifier();
                nModifier.setId(modifier.getId());
                nModifier.setDesc(modifier.getDesc());
                nModifier.setType(modifier.getType());

                modifiers.add(nModifier);

                adapter.notifyDataSetChanged();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceActivity.this);
        builder.setView(view);
        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                List<Modifier> unusedList = new ArrayList<>();
                for (Modifier modifier : orgModifiers) {
                    if (modifier.getType().length() == 0) {
                        unusedList.add(modifier);
                    }
                }

                for (Modifier modifier : unusedList) {
                    orgModifiers.remove(modifier);
                }

                expandableListView.expandGroup(expandableListAdapter.getSelectedIndex());
                expandableListAdapter.setSelectedIndex(-1);
                expandableListAdapter.notifyDataSetChanged();
            }
        });
        builder.show();
    }

    private void setGridAdapterToCategories() {
        if (DataManager.needReload()) {
            Toast.makeText(InvoiceActivity.this, R.string.msg_reload_data, Toast.LENGTH_LONG).show();
            return;
        }
        dummyItemList = new ArrayList<>();
        for (Category category : DataManager.getCategories().values()) {
            Log.e("categories","3"+category.getUrlImage());
            category.setDummyColor(R.drawable.card_background_orange);
            category.setDummyDesc(category.getId());
//            category.setUrlImage(category.getUrlImage());
//            category.setUrlImage("https://picsum.photos/id/1/200/300");
            dummyItemList.add(category);
        }

        gridView.setAdapter(new CategoriesAdapter(dummyItemList, InvoiceActivity.this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                setGridAdapterToItems(((Category) dummyItemList.get(i)).getId());
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("onItemLongClick","grooop");
                return false;
            }
        });
    }

    private void setGridAdapterToItems(String categoryID) {
        final List<DummyItem> dummyItemList = new ArrayList<>();
        final Collection<Item> items = DataManager.getItems(categoryID).values();
        for (Item item : items) {
            item.setDummyColor(R.drawable.card_background_blue_light);
            item.setDummyDesc(item.getName());
            item.setUrlImage(item.getimagePathItem());
            Log.e("setGridAdapterToItems","=="+item.getimagePathItem()+"\t"+item.getItemDescription());
            dummyItemList.add(item);
        }

        gridView.setAdapter(new CategoriesAdapter(dummyItemList, InvoiceActivity.this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item item = (Item) dummyItemList.get(i);
                String openItemIds = DataManager.getRegisters().get("WORK_WITH_OPEN_ITEM");
                if (openItemIds != null && Arrays.asList(openItemIds.split(",", -1)).contains(item.getId())) {
                    addOpenItem(item);
                } else {
                    addItem(item);
                }
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("onItemLongClick","items");
                Item item = (Item) dummyItemList.get(i);
                openItemInfoDialog(item.getItemDescription(),item.getUrlImage());
                return true;
            }
        });
    }

    private void openItemInfoDialog(String itemDescription,String pathImage) {
//        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
//                .setTitleText(this.getResources().getString(R.string.itemInfo))
//                .setContentText(itemDescription)
//                .show();
        AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceActivity.this);
        builder.setTitle(R.string.open_item);
        View view = getLayoutInflater().inflate(R.layout.item_info, null);
        builder.setView(view);

        final ImageView itemImageView = (ImageView) view.findViewById(R.id.itemImageView);
        final TextView itemInfo = (TextView) view.findViewById(R.id.itemInfo);
        Log.e("Picasso",""+pathImage);
        Picasso.get().load(pathImage).placeholder(R.drawable.burger22)
                .into(itemImageView);
        itemInfo.setText(itemDescription);


        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();


    }

    private void addOpenItem(final Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceActivity.this);
        builder.setTitle(R.string.open_item);
        View view = getLayoutInflater().inflate(R.layout.open_item_form, null);
        builder.setView(view);

        final EditText nameEditText = (EditText) view.findViewById(R.id.txtDescription);
        final EditText qtyEditText = (EditText) view.findViewById(R.id.txtQty);
        final EditText priceEditText = (EditText) view.findViewById(R.id.txtPrice);


        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (qtyEditText.getText().toString().length() == 0) {
                    qtyEditText.setText("0");
                }
                if (priceEditText.getText().toString().length() == 0) {
                    priceEditText.setText("0");
                }
                Item itemCopy = DataManager.getItemCopy(item);
                itemCopy.setName(nameEditText.getText().toString());
                itemCopy.setName2(itemCopy.getName());
                itemCopy.setDummyDesc(item.getName());
                itemCopy.setQty(Float.valueOf(qtyEditText.getText().toString()));
                itemCopy.setPrice(Float.valueOf(priceEditText.getText().toString()));
                addItem(itemCopy);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void addItem(Item item) {
        int index = expandableListAdapter.getSelectedIndex();
        if (index > -1) {
            if (currentTable[0].getItems().size() > index) {
                Item mItem = currentTable[0].getItems().get(index);
                if (!mItem.isOld() && mItem.getId().equals(item.getId())) {
                    mItem.setQty(mItem.getQty() + 1);
                    setTotals();
                    expandableListAdapter.notifyDataSetChanged();
                    return;
                }
            }
        }

        System.out.println(item.getId());

        Item newItem = DataManager.getItemCopy(item);

        if (newItem.isTimeItem())
        {
            Date openDate = new Date();
            String dateString = mySdf.format(openDate);
            try {
                openDate = mySdf.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            newItem.setOpenTime(openDate);
            newItem.setEndTime(openDate);
            newItem.setQty(1);
        }
        else
            newItem.setQty(item.getQty() == 0 ? 1 : item.getQty());

        boolean useForceQ = DataManager.getRegisters().get("USE_FORCEQUESTION").equals("1");
        for (Question question : newItem.getQuestions()) {
            if (!question.getType().equals("2") && !useForceQ)
                continue;
            answerQuestion(question, newItem);
        }
        currentTable[0].getItems().add(newItem);
        if (newItem.getQuestions().size() == 0) {
            expandableListView.setSelectedGroup(currentTable[0].getItems().size() - 1);
            expandableListAdapter.notifyDataSetChanged();
        }
        setTotals();
//        DataManager.serializeAndSaveTable(currentTable[0], InvoiceActivity.this);
    }

    private void setItemNote(final Item item) {
        if (!DataManager.getRegisters().get("USE_ITEMS_REMARK").equals("1"))
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceActivity.this);
        builder.setTitle(R.string.item_note);
        final EditText editText = new EditText(InvoiceActivity.this);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80));
        editText.setText(item.getNote());
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        builder.setView(editText);
        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                item.setNote(editText.getText().toString());
            }
        });
        builder.setNegativeButton(getString(R.string.back), null);
        builder.show();
    }
//dr7
    private void answerQuestion(final Question question, final Item item) {
        final AlertDialog dialog[] = {null};
        final AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceActivity.this);
        builder.setTitle(question.getDesc());
        final List<String> answers = new ArrayList<>();
        for (Answer answer : question.getAnswers()) {
            answers.add(answer.getDesc());
        }
        final boolean[] checkedItems = new boolean[answers.size()];
        if (question.getAnswersCount() == 0 && question.getType().equals("0")) {
            item.getAnswers().add(question.getAnswers().get(0));
            builder.setSingleChoiceItems(answers.toArray(new String[answers.size()]), 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    List<Answer> answersToRemove = new ArrayList<>();
                    for (Answer answer : item.getAnswers()) {
                        if (answer.getqId().equals(question.getId())) {
                            answersToRemove.add(answer);
                        }
                    }

                    for (Answer answer : answersToRemove) {
                        item.getAnswers().remove(answer);
                    }

                    item.getAnswers().add(question.getAnswers().get(i));
                }
            });
        }
        else if(question.getAnswersCount() == 0 && question.getType().equals("1")){
            builder.setMultiChoiceItems(answers.toArray(new String[answers.size()]), checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    checkedItems[i] = b;
                    if (b) {
                        int count = 0;
                        for (boolean checkedItem : checkedItems) {
                            if (checkedItem)
                                count++;
                        }

                    }


                    List<Answer> answersToRemove = new ArrayList<>();
                    for (Answer answer : item.getAnswers()) {
                        if (answer.getqId().equals(question.getId())) {
                            answersToRemove.add(answer);
                        }
                    }

                    for (Answer answer : answersToRemove) {
                        item.getAnswers().remove(answer);
                    }

                    for (int x = 0; x < question.getAnswers().size(); x++) {
                        if (checkedItems[x]) {
                            item.getAnswers().add(question.getAnswers().get(x));
                        }
                    }
                }
            });
        }
        else if (question.getAnswersCount() >= 1 || (question.getAnswersCount() == 0 && question.getType().equals("2"))) {//
            builder.setMultiChoiceItems(answers.toArray(new String[answers.size()]), checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    checkedItems[i] = b;
                    if (b) {
                        int count = 0;
                        for (boolean checkedItem : checkedItems) {
                            if (checkedItem)
                                count++;
                        }

                        if (count > question.getAnswersCount() && question.getType().equals("2")) {
                            Toast.makeText(InvoiceActivity.this, "Maximum answer count was reached", Toast.LENGTH_LONG).show();
                            checkedItems[i] = false;
                            dialog[0].getListView().setItemChecked(i, false);
                            return;
                        }
                    }


                    List<Answer> answersToRemove = new ArrayList<>();
                    for (Answer answer : item.getAnswers()) {
                        if (answer.getqId().equals(question.getId())) {
                            answersToRemove.add(answer);
                        }
                    }

                    for (Answer answer : answersToRemove) {
                        item.getAnswers().remove(answer);
                    }

                    for (int x = 0; x < question.getAnswers().size(); x++) {
                        if (checkedItems[x]) {
                            item.getAnswers().add(question.getAnswers().get(x));
                        }
                    }
                }
            });
        } else if (question.getAnswersCount() == -1 && question.getType().equals("2")) {
            builder.setMultiChoiceItems(answers.toArray(new String[answers.size()]), checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    checkedItems[i] = b;

                    List<Answer> answersToRemove = new ArrayList<>();
                    for (Answer answer : item.getAnswers()) {
                        if (answer.getqId().equals(question.getId())) {
                            answersToRemove.add(answer);
                        }
                    }

                    for (Answer answer : answersToRemove) {
                        item.getAnswers().remove(answer);
                    }

                    for (int x = 0; x < question.getAnswers().size(); x++) {
                        if (checkedItems[x]) {

                            item.getAnswers().add(question.getAnswers().get(x));
                        }
                    }
                }
            });
        }
        builder.setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (question.getAnswersCount() > 1) {
                    int count = 0;
                    for (Answer answer : item.getAnswers()) {
                        if (answer.getqId().equals(question.getId())) {
                            count++;
                        }
                    }

                    if (count < question.getAnswersCount() && question.getType().equals("2")) {
                        Toast.makeText(InvoiceActivity.this, "Not enough answers were provided", Toast.LENGTH_LONG).show();
                        InvoiceActivity.currentTable[0].getItems().remove(item);
                        setTotals();
                        return;
                    }


                } else {
                    if (question.getType().equals("2") && hideModifierRoot && !question.getAnswers().isEmpty()) {
                        InvoiceActivity.currentTable[0].getItems().remove(item);
                        for (Answer answer : item.getAnswers()) {
                            Item nItem = DataManager.getItemCopy(answer.getItem());
                            nItem.setQty(item.getQty());
                            addItem(nItem);
                        }
                        return;
                    }
                }
                expandableListAdapter.notifyDataSetChanged();
                if (expandableListAdapter.getItems().size() > 0) {
                    expandableListView.expandGroup(expandableListAdapter.getItems().size() - 1);
                    setTotals();
                }
            }
        });
        builder.setCancelable(false);
        dialog[0] = builder.show();
    }

    private class GetTableOrder extends AsyncTask<Void, Void, List<Item>> {

        @Override
        protected List<Item> doInBackground(Void... voids) {

            return DataManager.getTableOrder(currentTable[0].getId());
        }

        @Override
        protected void onPostExecute(final List<Item> items) {
            super.onPostExecute(items);

            itemsListViewAdapter = new ItemsListViewAdapter(InvoiceActivity.this, items);
            itemsListView.setAdapter(itemsListViewAdapter);

            if (items.isEmpty()) {
                clearItems.setVisibility(View.INVISIBLE);
            } else {
                clearItems.setVisibility(View.VISIBLE);
            }

            itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Item item = DataManager.getItems(null).get(items.get(i).getId());
                    Item nItem = DataManager.getItemCopy(item);
                    nItem.setNotes(items.get(i).getNotes());
                    nItem.setQty(item.getQty());
                    addItem(nItem);
                }
            });
        }
    }

    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(500);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }
}
