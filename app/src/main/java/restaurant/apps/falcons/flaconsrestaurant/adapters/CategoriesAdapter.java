package restaurant.apps.falcons.flaconsrestaurant.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import restaurant.apps.falcons.flaconsrestaurant.R;
import restaurant.apps.falcons.flaconsrestaurant.models.DummyItem;

import java.util.List;

/**
 * Created by pure_ on 31/07/2016.
 */
public class CategoriesAdapter extends BaseAdapter {
    private List<DummyItem> items;
    private Context context;

    public CategoriesAdapter(List<DummyItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.invoice_items_gridview_item_view, null);
        }

        TextView textView = (TextView) view.findViewById(R.id.msgContent);
        textView.setText(items.get(i).getDummyDesc());

        ImageView groupImageView=(ImageView) view.findViewById(R.id.groupImageView);
        textView.setBackgroundResource(items.get(i).getDummyColor());
        LinearLayout linearInvoice= (LinearLayout) view.findViewById(R.id.linearInvoice);
//        String img_url = "YOUR IMAGE URL";
//        if (!img_url.equalsIgnoreCase(""))
//            Picasso.with(context).load(img_url).placeholder(R.drawable.user_image)// Place holder image from drawable folder
//                    .error(R.drawable.user_image).resize(110, 110).centerCrop()
//                    .into("IMAGE VIEW ID WHERE YOU WANT TO SET IMAGE");
        Log.e("Picasso",""+items.get(i).getUrlImage());
        Picasso.get().load(items.get(i).getUrlImage()).placeholder(R.drawable.burger22)
                .fit()
                .centerCrop().into(groupImageView);
        return view;
    }
}
