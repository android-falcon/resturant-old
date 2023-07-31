package restaurant.apps.falcons.flaconsrestaurant.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import restaurant.apps.falcons.flaconsrestaurant.R;
import restaurant.apps.falcons.flaconsrestaurant.util.Constants;
import restaurant.apps.falcons.flaconsrestaurant.util.DataManager;

import java.util.Locale;

/**
 * Created by Salah on 8/6/2016.
 */

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = Constants.getSharedPrefs(this);
        final SharedPreferences.Editor editor = sp.edit();

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

        setContentView(R.layout.login_activity);

        final EditText password = (EditText) findViewById(R.id.password);
        Button login = (Button) findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passwordString = password.getText().toString();
                if (!passwordString.equals("")) {
                    boolean loginSuccessful = DataManager.checkLogin(passwordString, LoginActivity.this);
                    if (loginSuccessful) {
                        hideKeyboard();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.enter_password_first), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (sp.getString(Constants.ipAddress, "").equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.enter_server_ip));

            View ipAddressView = getLayoutInflater().inflate(R.layout.ip_address_view, null);
            final EditText firstPart = (EditText) ipAddressView.findViewById(R.id.first_part);
            final EditText secondPart = (EditText) ipAddressView.findViewById(R.id.second_part);
            final EditText thirdPart = (EditText) ipAddressView.findViewById(R.id.third_part);
            final EditText fourthPart = (EditText) ipAddressView.findViewById(R.id.fourth_part);

            firstPart.requestFocus();

            firstPart.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                    if (charSequence.length() == 3) {
                        firstPart.clearFocus();
                        secondPart.requestFocus();
                        secondPart.setCursorVisible(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            secondPart.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                    if (charSequence.length() == 3) {
                        secondPart.clearFocus();
                        thirdPart.requestFocus();
                        thirdPart.setCursorVisible(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            thirdPart.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                    if (charSequence.length() == 3) {
                        thirdPart.clearFocus();
                        fourthPart.requestFocus();
                        fourthPart.setCursorVisible(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            builder.setView(ipAddressView);
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.save), null);
            final AlertDialog mDialog = builder.create();
            mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    Button b = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String firstField = firstPart.getText().toString();
                            String secondField = secondPart.getText().toString();
                            String thirdField = thirdPart.getText().toString();
                            String fourthField = fourthPart.getText().toString();
                            if (!firstField.equals("") && !secondField.equals("") && !thirdField.equals("") && !fourthField.equals("")) {
                                String ipAddress = firstField + "." + secondField + "." + thirdField + "." + fourthField;
                                editor.putString(Constants.ipAddress, ipAddress).apply();
                                hideKeyboard();
                                dialog.dismiss();
                            } else
                                Toast.makeText(LoginActivity.this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            mDialog.show();
        }
        new MainActivity.loadData(LoginActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Toast.makeText(this, R.string.version, Toast.LENGTH_LONG).show();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}