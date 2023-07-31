package restaurant.apps.falcons.flaconsrestaurant.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;

import restaurant.apps.falcons.flaconsrestaurant.R;
import restaurant.apps.falcons.flaconsrestaurant.adapters.DialogValuesLvAdapter;
import restaurant.apps.falcons.flaconsrestaurant.util.Constants;
import restaurant.apps.falcons.flaconsrestaurant.util.DataManager;

import java.util.Locale;

/**
 * Created by Salah on 8/3/2016.
 */

public class SettingsActivity extends Activity implements View.OnClickListener {

    private ImageView availableColor;
    private ImageView usedColor;
    private ImageView finishSoonColor;
    private ImageView reservedColor;
    private ImageView lockedColor;
    private SharedPreferences.Editor editor;
    private String language;
    private int tempDeviceType;
    private ToggleButton sectionByPOSTButtons, dateByPOSTButtons,workTimeItemButtons,callCaptinButtons;
    private String sectionsByPOS, dateByPOS,timeItem,callCaptin;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = Constants.getSharedPrefs(this);
        editor = sp.edit();

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

        setContentView(R.layout.settings_activity);

        final TextView languageTV = (TextView) findViewById(R.id.language_button);
        if (language.equals("ar"))
            languageTV.setText("العربية");
        else
            languageTV.setText("English");
        languageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] languagesArray = new String[]{"العربية", "English"};

                android.app.AlertDialog.Builder changeLanguageDialog = new android.app.AlertDialog.Builder(SettingsActivity.this);
                changeLanguageDialog.setTitle(getString(R.string.change_language_title));
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mView = layoutInflater.inflate(R.layout.dialogs_list_view, null);
                changeLanguageDialog.setView(mView);
                final Dialog dialog = changeLanguageDialog.create();
                dialog.show();

                ListView listView = (ListView) mView.findViewById(R.id.dialog_values_list_view);
                DialogValuesLvAdapter dialogValuesLvAdapter = new DialogValuesLvAdapter(SettingsActivity.this, languagesArray);
                listView.setAdapter(dialogValuesLvAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        switch (position) {
                            case 0:
                                if (!language.equals("ar")) {
                                    languageTV.setText("العربية");
                                    editor.putString(Constants.language, "ar").apply();
                                    language = "ar";
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(getIntent());
                                    overridePendingTransition(0, 0);
                                }
                                break;
                            case 1:
                                if (!language.equals("en")) {
                                    languageTV.setText("English");
                                    editor.putString(Constants.language, "en").apply();
                                    language = "en";
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(getIntent());
                                    overridePendingTransition(0, 0);
                                }
                                break;
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        final TextView deviceTypeTv = (TextView) findViewById(R.id.device_type_button);
        tempDeviceType = DataManager.getDeviceType(this);
        if (tempDeviceType == 1)
            deviceTypeTv.setText(getString(R.string.take_out));
        else
            deviceTypeTv.setText(getString(R.string.dine_in));
        deviceTypeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] typesArray = new String[]{getString(R.string.dine_in), getString(R.string.take_out)};

                android.app.AlertDialog.Builder typesDialog = new android.app.AlertDialog.Builder(SettingsActivity.this);
                typesDialog.setTitle(R.string.select_type);
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mView = layoutInflater.inflate(R.layout.dialogs_list_view, null);
                typesDialog.setView(mView);
                final Dialog dialog = typesDialog.create();
                dialog.show();

                ListView listView = (ListView) mView.findViewById(R.id.dialog_values_list_view);
                DialogValuesLvAdapter dialogValuesLvAdapter = new DialogValuesLvAdapter(SettingsActivity.this, typesArray);
                listView.setAdapter(dialogValuesLvAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        switch (position) {
                            case 0:
                                deviceTypeTv.setText(typesArray[0]);
                                editor.putInt(Constants.deviceType, Constants.DEVICE_TYPE_DINE_IN).apply();
                                tempDeviceType = Constants.DEVICE_TYPE_DINE_IN;
                                break;
                            case 1:
                                deviceTypeTv.setText(typesArray[1]);
                                editor.putInt(Constants.deviceType, Constants.DEVICE_TYPE_TAKE_OUT).apply();
                                tempDeviceType = Constants.DEVICE_TYPE_DINE_IN;
                                break;
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        final EditText firstPart = (EditText) findViewById(R.id.first_part);
        final EditText secondPart = (EditText) findViewById(R.id.second_part);
        final EditText thirdPart = (EditText) findViewById(R.id.third_part);
        final EditText fourthPart = (EditText) findViewById(R.id.fourth_part);
        firstPart.requestFocus();

        final EditText posNO = (EditText) findViewById(R.id.pos_number_ed);
        posNO.setText(sp.getString(Constants.posNO, ""));

        final EditText deviceID = (EditText) findViewById(R.id.id_ed);
        deviceID.setText(DataManager.getDeviceID(SettingsActivity.this));

        final EditText storeNO = (EditText) findViewById(R.id.store_number_ed);
        storeNO.setText(sp.getString(Constants.storeNO, ""));

        sectionByPOSTButtons = (ToggleButton) findViewById(R.id.sectionByPOSTButtons);
        dateByPOSTButtons = (ToggleButton) findViewById(R.id.dateByPOSTButtons);
        workTimeItemButtons= (ToggleButton) findViewById(R.id.workTimeItemButtons);
        callCaptinButtons= (ToggleButton) findViewById(R.id.callCaptinButtons);
        sectionsByPOS = sp.getString(Constants.sectionsByPos, "");
        dateByPOS = sp.getString(Constants.dateByPosNo, "");
        timeItem=sp.getString(Constants.work_withTime, "");
         callCaptin=sp.getString(Constants.call_captin, "");

        if (sectionsByPOS.equals("1"))
        {
            sectionByPOSTButtons.setChecked(true);
        }
        else
        {
            sectionByPOSTButtons.setChecked(false);
        }

        if (dateByPOS.equals("1"))
        {
            dateByPOSTButtons.setChecked(true);
        }
        else
        {
            dateByPOSTButtons.setChecked(false);
        }
        if (timeItem.equals("1"))
        {
            workTimeItemButtons.setChecked(true);
        }
        else
        {
            workTimeItemButtons.setChecked(false);
        }
        if (callCaptin.equals("1"))
        {
            callCaptinButtons.setChecked(true);
        }
        else
        {
            callCaptinButtons.setChecked(false);
        }

        String ipAddress = sp.getString(Constants.ipAddress, "");
        if (!ipAddress.equals("")) {
            String[] strArr = ipAddress.split("\\.", -1);
            if (strArr.length > 3) {
                firstPart.setText(strArr[0]);
                secondPart.setText(strArr[1]);
                thirdPart.setText(strArr[2]);
                fourthPart.setText(strArr[3]);
            }
        }

        TextView save = (TextView) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstField = firstPart.getText().toString();
                String secondField = secondPart.getText().toString();
                String thirdField = thirdPart.getText().toString();
                String fourthField = fourthPart.getText().toString();
                if (!firstField.equals("") && !secondField.equals("") && !thirdField.equals("") && !fourthField.equals("")) {
                    String ipAddress = firstField + "." + secondField + "." + thirdField + "." + fourthField;
                    editor.putString(Constants.ipAddress, ipAddress).apply();
                    editor.putString(Constants.posNO, posNO.getText().toString()).apply();
                    editor.putString(Constants.storeNO, storeNO.getText().toString()).apply();
                    editor.putString(Constants.deviceID, deviceID.getText().toString().replace("'", "")).apply();

                    if (sectionByPOSTButtons.isChecked() == true)
                    {
                        editor.putString(Constants.sectionsByPos, "1").apply();
                    }
                    else
                    {
                        editor.putString(Constants.sectionsByPos, "0").apply();
                    }

                    if (dateByPOSTButtons.isChecked() == true)
                    {
                        editor.putString(Constants.dateByPosNo, "1").apply();
                    }
                    else
                    {
                        editor.putString(Constants.dateByPosNo, "0").apply();
                    }
                    if (workTimeItemButtons.isChecked() == true)
                    {
                        editor.putString(Constants.work_withTime, "1").apply();
                    }
                    else
                    {
                        editor.putString(Constants.work_withTime, "0").apply();
                    }
                    if (callCaptinButtons.isChecked() == true)
                    {
                        editor.putString(Constants.call_captin, "1").apply();
                    }
                    else
                    {
                        editor.putString(Constants.call_captin, "0").apply();
                    }

                    finish();
                } else
                    Toast.makeText(SettingsActivity.this, getString(R.string.please_complete_ip_address), Toast.LENGTH_LONG).show();
            }
        });

        TextView exit = (TextView) findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView updateData = (TextView) findViewById(R.id.update_data);
        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MainActivity.loadData(SettingsActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        availableColor = (ImageView) findViewById(R.id.available);
        usedColor = (ImageView) findViewById(R.id.used);
        finishSoonColor = (ImageView) findViewById(R.id.finish_soon);
        reservedColor = (ImageView) findViewById(R.id.reserved);
        lockedColor = (ImageView) findViewById(R.id.locked);

        String availColorPrefs = sp.getString(Constants.availableColor, "blue");
        String usedColorPrefs = sp.getString(Constants.usedColor, "green");
        String finishSoonColorPrefs = sp.getString(Constants.finishSoonColor, "yellow");
        String reservedColorPrefs = sp.getString(Constants.reservedColor, "red");
        String lockedColorPrefs = sp.getString(Constants.lockedColor, "grey");

        setColorForIV(availableColor, availColorPrefs);
        setColorForIV(usedColor, usedColorPrefs);
        setColorForIV(finishSoonColor, finishSoonColorPrefs);
        setColorForIV(reservedColor, reservedColorPrefs);
        setColorForIV(lockedColor, lockedColorPrefs);

        availableColor.setOnClickListener(this);
        usedColor.setOnClickListener(this);
        finishSoonColor.setOnClickListener(this);
        reservedColor.setOnClickListener(this);
        lockedColor.setOnClickListener(this);

        TextView idTextView = (TextView) findViewById(R.id.device_id);
        String id = idTextView.getText() + DataManager.getDeviceID(this);
        idTextView.setText(id);
    }

    private void setColorForIV(ImageView iv, String color) {
        switch (color) {
            case "blue":
                iv.setBackgroundResource(R.drawable.card_background_blue);
                break;
            case "green":
                iv.setBackgroundResource(R.drawable.card_background_green);
                break;
            case "yellow":
                iv.setBackgroundResource(R.drawable.card_background_yellow);
                break;
            case "red":
                iv.setBackgroundResource(R.drawable.card_background_red);
                break;
            case "grey":
                iv.setBackgroundResource(R.drawable.card_background_grey_dark);
                break;
        }
    }

    @Override
    public void onClick(final View colorView) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.pick_color));

        View mView = getLayoutInflater().inflate(R.layout.colors_dialog, null);
        ImageView blue = (ImageView) mView.findViewById(R.id.blue);
        ImageView green = (ImageView) mView.findViewById(R.id.green);
        ImageView yellow = (ImageView) mView.findViewById(R.id.yellow);
        ImageView red = (ImageView) mView.findViewById(R.id.red);
        ImageView grey = (ImageView) mView.findViewById(R.id.grey);
        builder.setView(mView);
        final AlertDialog dialog = builder.create();
        dialog.show();

        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (colorView.getId()) {
                    case R.id.available:
                        availableColor.setBackgroundResource(R.drawable.card_background_blue);
                        editor.putString(Constants.availableColor, "blue").apply();
                        break;
                    case R.id.used:
                        usedColor.setBackgroundResource(R.drawable.card_background_blue);
                        editor.putString(Constants.usedColor, "blue").apply();
                        break;
                    case R.id.finish_soon:
                        finishSoonColor.setBackgroundResource(R.drawable.card_background_blue);
                        editor.putString(Constants.finishSoonColor, "blue").apply();
                        break;
                    case R.id.reserved:
                        reservedColor.setBackgroundResource(R.drawable.card_background_blue);
                        editor.putString(Constants.reservedColor, "blue").apply();
                        break;
                    case R.id.locked:
                        lockedColor.setBackgroundResource(R.drawable.card_background_blue);
                        editor.putString(Constants.lockedColor, "blue").apply();
                        break;
                }
                dialog.dismiss();
            }
        });

        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (colorView.getId()) {
                    case R.id.available:
                        availableColor.setBackgroundResource(R.drawable.card_background_green);
                        editor.putString(Constants.availableColor, "green").apply();
                        break;
                    case R.id.used:
                        usedColor.setBackgroundResource(R.drawable.card_background_green);
                        editor.putString(Constants.usedColor, "green").apply();
                        break;
                    case R.id.finish_soon:
                        finishSoonColor.setBackgroundResource(R.drawable.card_background_green);
                        editor.putString(Constants.finishSoonColor, "green").apply();
                        break;
                    case R.id.reserved:
                        reservedColor.setBackgroundResource(R.drawable.card_background_green);
                        editor.putString(Constants.reservedColor, "green").apply();
                        break;
                    case R.id.locked:
                        lockedColor.setBackgroundResource(R.drawable.card_background_green);
                        editor.putString(Constants.lockedColor, "green").apply();
                        break;
                }
                dialog.dismiss();
            }
        });

        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (colorView.getId()) {
                    case R.id.available:
                        availableColor.setBackgroundResource(R.drawable.card_background_yellow);
                        editor.putString(Constants.availableColor, "yellow").apply();
                        break;
                    case R.id.used:
                        usedColor.setBackgroundResource(R.drawable.card_background_yellow);
                        editor.putString(Constants.usedColor, "yellow").apply();
                        break;
                    case R.id.finish_soon:
                        finishSoonColor.setBackgroundResource(R.drawable.card_background_yellow);
                        editor.putString(Constants.finishSoonColor, "yellow").apply();
                        break;
                    case R.id.reserved:
                        reservedColor.setBackgroundResource(R.drawable.card_background_yellow);
                        editor.putString(Constants.reservedColor, "yellow").apply();
                        break;
                    case R.id.locked:
                        lockedColor.setBackgroundResource(R.drawable.card_background_yellow);
                        editor.putString(Constants.lockedColor, "yellow").apply();
                        break;
                }
                dialog.dismiss();
            }
        });

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (colorView.getId()) {
                    case R.id.available:
                        availableColor.setBackgroundResource(R.drawable.card_background_red);
                        editor.putString(Constants.availableColor, "red").apply();
                        break;
                    case R.id.used:
                        usedColor.setBackgroundResource(R.drawable.card_background_red);
                        editor.putString(Constants.usedColor, "red").apply();
                        break;
                    case R.id.finish_soon:
                        finishSoonColor.setBackgroundResource(R.drawable.card_background_red);
                        editor.putString(Constants.finishSoonColor, "red").apply();
                        break;
                    case R.id.reserved:
                        reservedColor.setBackgroundResource(R.drawable.card_background_red);
                        editor.putString(Constants.reservedColor, "red").apply();
                        break;
                    case R.id.locked:
                        lockedColor.setBackgroundResource(R.drawable.card_background_red);
                        editor.putString(Constants.lockedColor, "red").apply();
                        break;
                }
                dialog.dismiss();
            }
        });

        grey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (colorView.getId()) {
                    case R.id.available:
                        availableColor.setBackgroundResource(R.drawable.card_background_grey_dark);
                        editor.putString(Constants.availableColor, "grey").apply();
                        break;
                    case R.id.used:
                        usedColor.setBackgroundResource(R.drawable.card_background_grey_dark);
                        editor.putString(Constants.usedColor, "grey").apply();
                        break;
                    case R.id.finish_soon:
                        finishSoonColor.setBackgroundResource(R.drawable.card_background_grey_dark);
                        editor.putString(Constants.finishSoonColor, "grey").apply();
                        break;
                    case R.id.reserved:
                        reservedColor.setBackgroundResource(R.drawable.card_background_grey_dark);
                        editor.putString(Constants.reservedColor, "grey").apply();
                        break;
                    case R.id.locked:
                        lockedColor.setBackgroundResource(R.drawable.card_background_grey_dark);
                        editor.putString(Constants.lockedColor, "grey").apply();
                        break;
                }
                dialog.dismiss();
            }
        });
    }
}
