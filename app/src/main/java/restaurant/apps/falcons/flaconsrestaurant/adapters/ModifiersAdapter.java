package restaurant.apps.falcons.flaconsrestaurant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import restaurant.apps.falcons.flaconsrestaurant.R;
import restaurant.apps.falcons.flaconsrestaurant.models.Modifier;

import java.util.List;

/**
 * Created by pure_ on 01/08/2016.
 */
public class ModifiersAdapter extends BaseAdapter {
    private List<Modifier> modifiers;
    private Context context;

    public ModifiersAdapter(List<Modifier> modifiers, Context context) {
        this.modifiers = modifiers;
        this.context = context;
    }

    @Override
    public int getCount() {
        return modifiers.size();
    }

    @Override
    public Object getItem(int i) {
        return modifiers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.items_gridview_modifiers, null);
        }

        Modifier modifier = modifiers.get(i);

        TextView textView = (TextView) view.findViewById(R.id.msgContent);
        textView.setText(modifier.getDesc());

        textView = (TextView) view.findViewById(R.id.msgContent2);
        if (modifier.getType().length() == 0) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(modifier.getType());
        }

        return view;
    }
}
