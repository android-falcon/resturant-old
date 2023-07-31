package restaurant.apps.falcons.flaconsrestaurant.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import restaurant.apps.falcons.flaconsrestaurant.R;
import restaurant.apps.falcons.flaconsrestaurant.models.Table;
import restaurant.apps.falcons.flaconsrestaurant.models.User;
import restaurant.apps.falcons.flaconsrestaurant.util.ConnectionManager;
import restaurant.apps.falcons.flaconsrestaurant.util.Constants;
import restaurant.apps.falcons.flaconsrestaurant.util.DataManager;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Salah on 8/3/2016.
 */

public class TablesActivity extends AppCompatActivity {

    private float dX;
    private float dY;
    private RelativeLayout tablesLayout;
//    private  LinearLayout tablesLayout;
    private List<Table> tables;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Thread serviceThread;
    private static Date lastClickTime;

    private void startInvoice(final Table table) {


        /*SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        if (lastClickTime == null)
            lastClickTime = new Date();
        else
        {
            Date clickTime =  new Date();
            float diff = (clickTime.getTime() - lastClickTime.getTime())/1000;
            if (diff < 10)
            {
                Toast.makeText(getApplicationContext(), R.string.wait, Toast.LENGTH_SHORT).show();
                return;
            }
            else
            {
                lastClickTime = clickTime;
            }

            //long difference = java.Date.Duration.between(lastClickTime, clickTime).toMillis(); ;
        }*/ //dr7

        final ProgressDialog progressDialog = new ProgressDialog(TablesActivity.this);
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread() {
            @Override
            public void run() {

                while (true) {
                    if (!DataManager.isLoading) {
                        break;
                    }
                }

                if (serviceThread != null) {
                    serviceThread.interrupt();
                }

                String server = sp.getString(Constants.ipAddress, "");
                int port = 8085;
                if (!DataManager.pingHost(server, port, 5000)) {
                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), R.string.unable_to_connect,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }



                if (!DataManager.isTableLocked(TablesActivity.this, table, false)) {

                    if (DataManager.getRegisters().get("USE_TABLE_SECURITY").equals("1")) {
                        table.setUser("");
                        DataManager.getTableVHFNO(table, getApplicationContext());
                        if (table.getUser() != null && table.getUser().length() > 0 && !table.getUser().equals(DataManager.getCurrentUser(TablesActivity.this).getId())) {
                            runOnUiThread(new TimerTask() {
                                @Override
                                public void run() {
                                    User user = DataManager.getUsers().get(table.getUser());
                                    Toast.makeText(TablesActivity.this, "Table is locked for " + (user == null ? table.getUser() + "" : user.getName()), Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });
                            return;
                        }
                    }

                    DataManager.lockTable(table, TablesActivity.this);
                    table.setOrderID("");
                    table.setUser(DataManager.getCurrentUser(TablesActivity.this).getId());
                    DataManager.getTableData(table, TablesActivity.this);

                    while (true) {
                        if (!DataManager.isLoading) {
                            break;
                        }
                    }

                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {

                            progressDialog.dismiss();
                            InvoiceActivity.currentTable[0] = table;
                            Intent intent = new Intent(TablesActivity.this, InvoiceActivity.class);
                            startActivityForResult(intent, 0);
                        }
                    });

                } else {
                    progressDialog.dismiss();
                    startServiceThread();
                }
            }
        }.start();
    }

    private class loadData extends AsyncTask<Void, Void, Void> {

        //ProgressDialog progressDialog;
        Context context;

        loadData(Context context) {
            this.context = context;
        }
        HashMap<String, Table> oldTables = new HashMap<>();
        @Override
        protected void onPreExecute() {
            /*progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(context.getString(R.string.please_wait));
            progressDialog.setMessage(context.getString(R.string.loading_data));
            progressDialog.show();*/
            oldTables.clear();
            for (Table table : tables) {
                oldTables.put(table.getId(), table);

            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                SharedPreferences sp = Constants.getSharedPrefs(context);
                ConnectionManager.serverURL = "http://" + sp.getString(Constants.ipAddress, "") + ":8085/resturant";
                DataManager.readTablesOnly(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {

               /* if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
*/
                tables = DataManager.getTables(currentSection);
                for (Table table : tables) {
                    if (oldTables.containsKey(table.getId())) {
                        table.setX(oldTables.get(table.getId()).getX());
                        table.setY(oldTables.get(table.getId()).getY());
                    }
                }

                for (Table table : tables) {
                    View view = tablesViews.get(table.getId());
                    if (view == null)
                        continue;
                    table.setView(view);
                    View tv = view.findViewById(R.id.item_content);
                    tv.setBackgroundResource(table.getColor());
                }

                //TablesActivity.this.recreate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        startServiceThread();
    }

    private List<String> keys;
    private List<String> values;
    private String currentSection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = Constants.getSharedPrefs(this);

        String language = sp.getString(Constants.language, "en");
        Locale locale;
        if (language.equals("ar"))
            locale = new Locale("ar");
        else
            locale = new Locale("en_US");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, null);

        setContentView(R.layout.tables_activity);

        keys = new ArrayList<>(DataManager.getSections().keySet());
        values = new ArrayList<>(DataManager.getSections().values());

        final Switch edit = (Switch) findViewById(R.id.edit_switch);
        if (!DataManager.getCurrentUser(TablesActivity.this).getMaster().equals("1")) {
            edit.setEnabled(false);
        }
        edit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (edit.isChecked()) {
                    for (Table table : tables) {
                        table.getView().setOnTouchListener(new MyOnTouchListener(table));
                        table.getView().setOnClickListener(null);
                    }
                } else {
                    for (final Table table : tables) {
                        table.getView().setOnTouchListener(null);
                        table.getView().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                table.getView().setEnabled(false);
                                table.getView().setVisibility(View.INVISIBLE);

                                startInvoice(table);
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new TimerTask() {
                                            @Override
                                            public void run() {
                                                table.getView().setEnabled(true);
                                                table.getView().setVisibility(View.VISIBLE);
                                            }
                                        });
                                    }
                                }, 1000);
                            }
                        });
                    }
                }
            }
        });


        final Spinner sectionsSpinner = (Spinner) findViewById(R.id.section_value);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                values); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sectionsSpinner.setAdapter(spinnerArrayAdapter);

        sectionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentSection = sectionsSpinner.getSelectedItem().toString();
                currentSection = keys.get(values.indexOf(currentSection));
                createItems(currentSection);

                if (edit.isChecked()) {
                    for (Table table : tables) {
                        table.getView().setOnTouchListener(new MyOnTouchListener(table));
                        table.getView().setOnClickListener(null);
                    }
                } else {
                    for (final Table table : tables) {
                        table.getView().setOnTouchListener(null);
                        table.getView().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                table.getView().setEnabled(false);
                                table.getView().setVisibility(View.INVISIBLE);

                                startInvoice(table);
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new TimerTask() {
                                            @Override
                                            public void run() {
                                                table.getView().setEnabled(true);
                                                table.getView().setVisibility(View.VISIBLE);

                                            }
                                        });
                                    }
                                }, 1000);
                            }
                        });
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tablesLayout = (RelativeLayout) findViewById(R.id.tables_layout);
//        tablesLayout = (LinearLayout) findViewById(R.id.tables_layout);

//        try {
           String  currentSection = sectionsSpinner.getSelectedItem().toString();// errrrroeeee
            currentSection = keys.get(values.indexOf(currentSection));
            createItems(currentSection);
//        }catch (Exception e){
//            Log.e("currentSection",""+currentSection+"\t"+e.getMessage());
//            Toast.makeText(this, ""+getResources().getString(R.string.unable_to_connect), Toast.LENGTH_SHORT).show();
//        }




        Button save = (Button) findViewById(R.id.save);
        final Context myContext = this;
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp = Constants.getSharedPrefs(myContext);
                editor = sp.edit();
                for (Table table : tables) {
                    if (table.getX() != 0f) {
                        System.out.println(table.getId());
                    }
                    editor.putFloat(table.getId() + "-dx", table.getX());
                    editor.putFloat(table.getId() + "-dy", table.getY());
                }
                editor.apply();
                Toast.makeText(TablesActivity.this, R.string.changes_saved, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        for (final Table table : tables) {
            table.getView().setOnTouchListener(null);
            table.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    table.getView().setEnabled(false);
                    table.getView().setVisibility(View.INVISIBLE);

                    startInvoice(table);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new TimerTask() {
                                @Override
                                public void run() {
                                    table.getView().setEnabled(true);
                                    table.getView().setVisibility(View.VISIBLE);

                                }
                            });
                        }
                    }, 1000);
                }
            });
        }

        TextView homeTv = (TextView) findViewById(R.id.home);
        homeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startServiceThread();
            }
        }, 1000);
    }

    private void startServiceThread() {
        if (serviceThread != null) {
            serviceThread.interrupt();
        }
        serviceThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {

                        if (interrupted()) {
                            break;
                        }
                        runOnUiThread(new TimerTask() {
                            @Override
                            public void run() {
                                new loadData(TablesActivity.this).execute();
                            }
                        });

                        try {
                            if (interrupted()) {
                                break;
                            }
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        serviceThread.start();
    }

    @Override
    public void onBackPressed() {
        try {
            serviceThread.interrupt();
        } catch (Exception e) {

        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        serviceThread.interrupt();
        super.onDestroy();
    }

    private HashMap<String, View> tablesViews;

    private void createItems(String section) {
        if (tablesViews == null)
            tablesViews = new HashMap<>();
        else
            tablesViews.clear();

        Display display = getWindowManager().getDefaultDisplay();
        int maxWidth = display.getWidth() - 10;
        Log.e("maxWidth",""+maxWidth);
        LayoutInflater inflater = LayoutInflater.from(this);

        int widthSoFar = 0;
        int rowNum = 0;
        int colNum = -1;
        tables = DataManager.getTables(section);
        tablesLayout.removeAllViews();
        int height = 0;
//        Log.e("Table","size=="+tables.size()+"\t"+tables.get(tables.size()-1).getId());
        for (Table table : tables) {
            Log.e("Table","item=="+tables.size()+"\t"+table.getId());
            View view = inflater.inflate(R.layout.tables_item_view, null);
            TextView textView = (TextView) view.findViewById(R.id.item_content);
            textView.setText(table.getId());
            Log.e("Table","size=="+table.getColor());
            textView.setBackgroundResource(table.getColor());
            tablesLayout.addView(view);

            view.measure(0, 0);
            widthSoFar += view.getMeasuredWidth();
          //  Log.e("widthSoFar",""+widthSoFar);

            if (widthSoFar >= maxWidth) {
                colNum = 0;
                rowNum++;
                widthSoFar = view.getMeasuredWidth();
                Log.e("rowNum",""+rowNum);

            } else
            {
                colNum++;
                view.measure(0, 0);
            }


            float dy;
            float dx;

            table.setX(sp.getFloat(table.getId() + "-dx", 0f));
            table.setY(sp.getFloat(table.getId() + "-dy", 0f));
            /*table.setX(0f);
            table.setY(0f);*/

            if (table.getX() == 0 && table.getY() == 0) {
                dy = rowNum * view.getMeasuredHeight();
                dx = colNum * view.getMeasuredWidth();
            } else {
                dy = table.getY();
                dx = table.getX();
            }

            view.setX(dx);
            view.setY(dy);
            height = view.getMeasuredHeight();
            table.setView(view);
            tablesViews.put(table.getId(), view);
        }
        //tablesLayout.getLayoutParams().height = rowNum * height + height * 2;
    }

    private class MyOnTouchListener implements View.OnTouchListener {

        private Table table;

        MyOnTouchListener(Table table) {
            this.table = table;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    dX = view.getX() - event.getRawX();
                    dY = view.getY() - event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    view.animate().x(event.getRawX() + dX).y(event.getRawY() + dY).setDuration(0).start();
                    break;
                case MotionEvent.ACTION_UP:
                    float dx = event.getRawX() + dX;
                    float dy = event.getRawY() + dY;
                    table.setX(dx);
                    table.setY(dy);
                    break;
                default:
                    return false;
            }
            return true;
        }
    }
}
