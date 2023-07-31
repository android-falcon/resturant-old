package restaurant.apps.falcons.flaconsrestaurant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import restaurant.apps.falcons.flaconsrestaurant.R;
import restaurant.apps.falcons.flaconsrestaurant.models.Item;
import restaurant.apps.falcons.flaconsrestaurant.util.DataManager;

import java.util.HashMap;
import java.util.List;

/**
 * Created by pure_ on 26/10/2016.
 */

public class ItemsListViewAdapter extends BaseAdapter {

    private Context context;
    private List<Item> items;
    private LayoutInflater inflater;
    private HashMap<String, Item> itemsMap;

    public ItemsListViewAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemsMap = DataManager.getItems(null);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.items_listview_item_view, null);
        }

        TextView nameTv = (TextView) view.findViewById(R.id.text1);
        TextView qty = (TextView) view.findViewById(R.id.text3);

        TextView price = (TextView) view.findViewById(R.id.text2);
        price.getLayoutParams().width = 0;

        TextView itemNotes = (TextView) view.findViewById(R.id.item_notes);

        Item item = itemsMap.get(items.get(position).getId());

        String nameStr = "Not Available";
        String noteStr = "";
        if (item != null) {
            nameStr = item.getName();
            noteStr = items.get(position).getNotes() != null && !items.get(position).getNotes().equals("null") && !items.get(position).getNotes().equals("") ? items.get(position).getNotes() : "";
        }

        nameTv.setText(nameStr);
        qty.setText(String.valueOf(items.get(position).getQty()));
        itemNotes.setText(noteStr);

        if (!noteStr.equals("")) {
            itemNotes.setVisibility(View.VISIBLE);
        } else {
            itemNotes.setVisibility(View.GONE);
        }

        return view;
    }
}
