package restaurant.apps.falcons.flaconsrestaurant.activities;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import restaurant.apps.falcons.flaconsrestaurant.R;
import restaurant.apps.falcons.flaconsrestaurant.models.Table;
import restaurant.apps.falcons.flaconsrestaurant.util.ConnectionManager;
import restaurant.apps.falcons.flaconsrestaurant.util.Constants;
import restaurant.apps.falcons.flaconsrestaurant.util.DataManager;
import restaurant.apps.falcons.flaconsrestaurant.util.MyService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.datatype.Duration;

import static restaurant.apps.falcons.flaconsrestaurant.util.Constants.DEVICE_TYPE_TAKE_OUT;

public class MainActivity extends AppCompatActivity {
    private String language;
    private TextView tablesTv;
    private boolean isTakeOut;
    private static Date lastClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = Constants.getSharedPrefs(this);
        language = sp.getString(Constants.language, "en");
        Locale locale;
        if (language.equals("ar"))
            locale = new Locale("ar");
        else
            locale = new Locale("en_US");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, null);

        setContentView(R.layout.activity_main);

        ensureServiceIsRunning();

        tablesTv = (TextView) findViewById(R.id.tables);
        isTakeOut = DataManager.getDeviceType(MainActivity.this) == DEVICE_TYPE_TAKE_OUT;
        if (!isTakeOut) {
            tablesTv.setText(R.string.tables);
        } else {
            tablesTv.setText(R.string.take_out);
        }
        tablesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                boolean isTakeOut = DataManager.getDeviceType(MainActivity.this) == DEVICE_TYPE_TAKE_OUT;
                if (!isTakeOut)
                    new loadData(MainActivity.this, "tables").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                else {

                    Intent invoiceIntent = new Intent(MainActivity.this, InvoiceActivity.class);
                    Table table = new Table();
                    table.setId(-1 + "");
                    table.setSection(-1 + "");
                    table.setUser(DataManager.getCurrentUser(MainActivity.this).getId());
                    InvoiceActivity.currentTable[0] = table;
                    startActivity(invoiceIntent);
                }
            }
        });

        TextView settingsTv = (TextView) findViewById(R.id.settings);
        settingsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataManager.getCurrentUser(MainActivity.this) == null) return;
                if (DataManager.getCurrentUser(MainActivity.this).getMaster().equals("1")) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
            }
        });

        TextView logOut = (TextView) findViewById(R.id.logout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataManager.logout("");
                finish();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        TextView reviews = (TextView) findViewById(R.id.reviews);
        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataManager.getCurrentUser(MainActivity.this).getMaster().equals("1")) {
                    Intent intent = new Intent(MainActivity.this, ReviewsActivity.class);
                    startActivity(intent);
                }
            }
        });
        if (DataManager.getCurrentUser(MainActivity.this).getMaster().equals("1")) {
            reviews.setVisibility(View.GONE);
        }

        TextView updateData = (TextView) findViewById(R.id.update_data);
        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new loadData(MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        TextView userTextView = (TextView) findViewById(R.id.user_title);
        if (DataManager.getCurrentUser(MainActivity.this) == null) {
            finish();
        }
        userTextView.setText(DataManager.getCurrentUser(MainActivity.this).getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = Constants.getSharedPrefs(this);
        String language = sp.getString(Constants.language, "en");

        boolean isTakeOut = DataManager.getDeviceType(MainActivity.this) == DEVICE_TYPE_TAKE_OUT;
        if (!language.equals(this.language)) {
            Locale locale;
            if (language.equals("ar"))
                locale = new Locale("ar");
            else
                locale = new Locale("en_US");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config, null);

            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        } else if (isTakeOut != this.isTakeOut) {
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
    }

    public static class loadData extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        Context context;
        String caller;

        loadData(Context context) {
            this.context = context;
        }

        loadData(Context context, String caller) {
            this.context = context;
            this.caller = caller;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(context.getString(R.string.please_wait));
            progressDialog.setMessage(context.getString(R.string.loading_data));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                SharedPreferences sp = Constants.getSharedPrefs(context);
                ConnectionManager.serverURL = "http://" + sp.getString(Constants.ipAddress, "") + ":8085/resturant";
                DataManager.readAllPHP(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (caller != null) {
                    if (caller.equals("tables")) {
                        Intent intent = new Intent(context, TablesActivity.class);
                        context.startActivity(intent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void ensureServiceIsRunning() {
        if (!isServiceRunning(MyService.class)) {
            startService(new Intent(this, MyService.class));
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if (service.service.getClassName().contains(serviceClass.getName())) return true;
        return false;
    }
}
