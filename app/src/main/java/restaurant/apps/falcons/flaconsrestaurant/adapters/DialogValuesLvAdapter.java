package restaurant.apps.falcons.flaconsrestaurant.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import restaurant.apps.falcons.flaconsrestaurant.R;
import restaurant.apps.falcons.flaconsrestaurant.util.Constants;

/**
 * Created by Salah on 7/17/2016.
 */

public class DialogValuesLvAdapter extends BaseAdapter {

    private Context context;
    String[] minutesArray;
    private static LayoutInflater inflater = null;

    public DialogValuesLvAdapter(Context context, String[] minutesArray) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.minutesArray = minutesArray;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return minutesArray.length;
    }

    public Object getItem(int position) {
        return null;
    }

    private class Holder {
        TextView value;
    }

    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    public View getView(final int position, View convertView, ViewGroup parent) {
        DialogValuesLvAdapter.Holder holder = new DialogValuesLvAdapter.Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.dialog_list_view_item, null);

        holder.value = (TextView) rowView.findViewById(R.id.values_tv);

        SharedPreferences sp = Constants.getSharedPrefs(context);
        String language = sp.getString(Constants.language, "en");
        if (language.equals("ar"))
            holder.value.setGravity(Gravity.RIGHT);
        else
            holder.value.setGravity(Gravity.LEFT);

        holder.value.setText(minutesArray[position]);

        return rowView;
    }
}